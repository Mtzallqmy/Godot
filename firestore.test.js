const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const STUDENT_UID = "student_alice_101";
const OTHER_UID = "student_bob_202";
const EDITOR_UID = "editor_tariq_303";
const REVIEWER_UID = "reviewer_sara_404";
const ADMIN_UID = "admin_moataz_505";
const ADMIN_EMAIL = "zydealwajee712@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(STUDENT_UID).get());
  await assertFails(unauthDb.collection("cmsContent").where("status", "==", "PUBLISHED").get());
  await assertFails(unauthDb.collection("auditLogs").get());
});

test("Student profile: owner can create STUDENT profile, update progress, and cannot self-escalate role or spoof another user", async () => {
  const aliceDb = testEnv.authenticatedContext(STUDENT_UID).firestore();
  const bobDb = testEnv.authenticatedContext(OTHER_UID).firestore();
  const created = new Date(Date.now() - 5000);

  // Reject self-assigned ADMIN role on create
  await assertFails(
    aliceDb.collection("users").doc(STUDENT_UID).set({
      userId: STUDENT_UID,
      displayName: "Alice",
      role: "ADMIN",
      preferredLanguage: "en",
      completedLessonIds: [],
      savedStructureIds: [],
      bookmarkedTermIds: [],
      createdAt: created,
      updatedAt: created,
    })
  );

  // Valid STUDENT profile create succeeds
  await assertSucceeds(
    aliceDb.collection("users").doc(STUDENT_UID).set({
      userId: STUDENT_UID,
      displayName: "Alice Student",
      role: "STUDENT",
      preferredLanguage: "ar",
      completedLessonIds: ["lesson_cardiac_cycle"],
      savedStructureIds: ["heart", "aorta"],
      bookmarkedTermIds: ["term_heart"],
      createdAt: created,
      updatedAt: created,
    })
  );

  // Valid partial progress update succeeds
  await assertSucceeds(
    aliceDb.collection("users").doc(STUDENT_UID).update({
      completedLessonIds: ["lesson_cardiac_cycle", "lesson_ventilation"],
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject role escalation on update
  await assertFails(
    aliceDb.collection("users").doc(STUDENT_UID).update({
      role: "ADMIN",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject future timestamp tampering
  await assertFails(
    aliceDb.collection("users").doc(STUDENT_UID).update({
      displayName: "Future Tamper",
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  // Reject cross-user read and write
  await assertFails(bobDb.collection("users").doc(STUDENT_UID).get());
  await assertFails(bobDb.collection("users").doc(STUDENT_UID).delete());

  // Owner can delete own learning record (privacy data deletion right)
  await assertSucceeds(aliceDb.collection("users").doc(STUDENT_UID).delete());
});

test("Private user subcollections (quizAttempts & activityLog): owner isolated, immutable, cross-user denied", async () => {
  const aliceDb = testEnv.authenticatedContext(STUDENT_UID).firestore();
  const bobDb = testEnv.authenticatedContext(OTHER_UID).firestore();
  const now = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(STUDENT_UID)
      .collection("quizAttempts")
      .doc("attempt_01")
      .set({
        userId: STUDENT_UID,
        subject: "cardiovascular",
        difficulty: "INTERMEDIATE",
        score: 4,
        totalQuestions: 5,
        createdAt: now,
      })
  );

  // Reject invalid score > totalQuestions
  await assertFails(
    aliceDb
      .collection("users")
      .doc(STUDENT_UID)
      .collection("quizAttempts")
      .doc("attempt_bad")
      .set({
        userId: STUDENT_UID,
        subject: "cardiovascular",
        difficulty: "INTERMEDIATE",
        score: 9,
        totalQuestions: 5,
        createdAt: now,
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(STUDENT_UID)
      .collection("activityLog")
      .doc("act_01")
      .set({
        userId: STUDENT_UID,
        activityType: "PATHOLOGY",
        targetId: "aortic_stenosis",
        titleEn: "Explored Aortic Stenosis",
        titleAr: "استكشف تضيق الصمام الأبهري",
        createdAt: now,
      })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(STUDENT_UID).collection("quizAttempts").get()
  );
  await assertFails(
    bobDb.collection("users").doc(STUDENT_UID).collection("quizAttempts").get()
  );
});

test("Medical CMS Editorial Governance: DRAFT -> PENDING_REVIEW -> APPROVED -> PUBLISHED role boundaries & email spoofing guard", async () => {
  // Seed role registries via rules-disabled admin setup
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await db.collection("editors").doc(EDITOR_UID).set({ active: true });
    await db.collection("reviewers").doc(REVIEWER_UID).set({ active: true });
  });

  const studentDb = testEnv.authenticatedContext(STUDENT_UID).firestore();
  const editorDb = testEnv.authenticatedContext(EDITOR_UID).firestore();
  const reviewerDb = testEnv.authenticatedContext(REVIEWER_UID).firestore();
  const spoofedAdminDb = testEnv
    .authenticatedContext("spoof_uid", { email: ADMIN_EMAIL, email_verified: false })
    .firestore();
  const verifiedAdminDb = testEnv
    .authenticatedContext(ADMIN_UID, { email: ADMIN_EMAIL, email_verified: true })
    .firestore();

  const created = new Date(Date.now() - 8000);
  const draftPayload = {
    titleEn: "Mitral Valve Regurgitation Overview",
    titleAr: "نظرة عامة على ارتجاع الصمام التاجي",
    contentType: "PATHOLOGY",
    systemId: "cardiovascular",
    summaryEn: "Educational overview of backward systolic flow across an incompetent mitral valve.",
    summaryAr: "نظرة تعليمية عامة حول ارتداد الدم الانقباضي عبر صمام تاجي غير محكم الإغلاق.",
    scientificReferences: "NIH NHLBI Heart Valve Diseases; AHA Clinical Education",
    status: "DRAFT",
    authorUid: EDITOR_UID,
    authorName: "Tariq Editor",
    version: 1,
    createdAt: created,
    updatedAt: created,
  };

  // Student cannot create CMS drafts; spoofed unverified admin email cannot create drafts
  await assertFails(studentDb.collection("cmsContent").doc("entry_1").set(draftPayload));
  await assertFails(
    spoofedAdminDb
      .collection("cmsContent")
      .doc("entry_spoof")
      .set({ ...draftPayload, authorUid: "spoof_uid" })
  );

  // Editor cannot bypass directly into PUBLISHED
  await assertFails(
    editorDb
      .collection("cmsContent")
      .doc("entry_bypass")
      .set({ ...draftPayload, status: "PUBLISHED" })
  );

  // Editor creates DRAFT
  await assertSucceeds(editorDb.collection("cmsContent").doc("entry_1").set(draftPayload));

  // Student cannot read DRAFT or run unfiltered cmsContent list
  await assertFails(studentDb.collection("cmsContent").doc("entry_1").get());
  await assertFails(studentDb.collection("cmsContent").get());

  // Editor submits DRAFT -> PENDING_REVIEW (and cannot jump to PUBLISHED)
  await assertFails(
    editorDb.collection("cmsContent").doc("entry_1").update({
      status: "PUBLISHED",
      updatedAt: new Date(Date.now() - 4000),
    })
  );
  await assertSucceeds(
    editorDb.collection("cmsContent").doc("entry_1").update({
      status: "PENDING_REVIEW",
      updatedAt: new Date(Date.now() - 4000),
    })
  );

  // Reviewer approves PENDING_REVIEW -> APPROVED (cannot publish directly)
  await assertFails(
    reviewerDb.collection("cmsContent").doc("entry_1").update({
      status: "PUBLISHED",
      reviewerUid: REVIEWER_UID,
      reviewerName: "Dr. Sara Reviewer",
      updatedAt: new Date(Date.now() - 3000),
    })
  );
  await assertSucceeds(
    reviewerDb.collection("cmsContent").doc("entry_1").update({
      status: "APPROVED",
      reviewerUid: REVIEWER_UID,
      reviewerName: "Dr. Sara Reviewer",
      updatedAt: new Date(Date.now() - 3000),
    })
  );

  // Verified Admin publishes APPROVED -> PUBLISHED
  await assertSucceeds(
    verifiedAdminDb.collection("cmsContent").doc("entry_1").update({
      status: "PUBLISHED",
      updatedAt: new Date(Date.now() - 2000),
    })
  );

  // Now students can read PUBLISHED record and run filtered PUBLISHED query
  await assertSucceeds(studentDb.collection("cmsContent").doc("entry_1").get());
  await assertSucceeds(
    studentDb.collection("cmsContent").where("status", "==", "PUBLISHED").get()
  );

  // Audit log: append-only for editors/admins, forbidden for students
  await assertSucceeds(
    verifiedAdminDb.collection("auditLogs").doc("log_1").set({
      actorUid: ADMIN_UID,
      actorName: "Admin Moataz",
      action: "PUBLISH",
      targetEntryId: "entry_1",
      newStatus: "PUBLISHED",
      note: "Verified NIH citations and bilingual terminology.",
      createdAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(studentDb.collection("auditLogs").doc("log_1").get());
  await assertFails(verifiedAdminDb.collection("auditLogs").doc("log_1").delete());
});
