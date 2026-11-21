package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.ai.TutorInstructionMode
import com.example.ai.TutorMessage
import com.example.ai.ValidatedTutorAction
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.ui.theme.MedAlertContainer
import com.example.ui.theme.MedAlertRed
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedPendingContainer
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.theme.MedVerifiedGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiMedicalTutorScreen(
    messages: List<TutorMessage>,
    activeContextLabel: String,
    selectedMode: TutorInstructionMode,
    isStreaming: Boolean,
    language: AppLanguage,
    onSelectMode: (TutorInstructionMode) -> Unit,
    onSendQuestion: (String) -> Unit,
    onRetryLast: () -> Unit,
    onClearChat: () -> Unit,
    onExecuteAction: (ValidatedTutorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }

    val quickPrompts = if (language == AppLanguage.ARABIC) {
        listOf(
            "أرني الصمام التاجي واشرح وظيفته",
            "اشرح الفرق بين البطين الأيمن والأيسر",
            "كيف تعمل حاصرات بيتا-1 على القلب؟",
            "قارن بين تضيق الصمام الأبهري والصمام السليم"
        )
    } else {
        listOf(
            "Show me the mitral valve and explain its role",
            "Compare Left Ventricle vs Right Ventricle pressure",
            "How do Loop Diuretics inhibit NKCC2?",
            "Explain Aortic Stenosis pathophysiology simply"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_medical_tutor_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header + Active Medical Context Pill + Mode Chips
        Surface(
            color = MedDeepNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MedCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = UiStrings.modAiTutor.get(language),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == AppLanguage.ARABIC) {
                                    "مدعوم بـ Firebase AI Logic • سياق موثق من قاعدة المعرفة"
                                } else {
                                    "Powered by Firebase AI Logic • Grounded in verified MedVerse records"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MedTeal
                            )
                        }
                    }
                    if (messages.isNotEmpty()) {
                        IconButton(
                            onClick = onClearChat,
                            modifier = Modifier.testTag("ai_clear_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = if (language == AppLanguage.ARABIC) "مسح المحادثة" else "Clear conversation",
                                tint = MedTextSecondary
                            )
                        }
                    }
                }

                // Active Screen Context Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MedCyanContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = MedCyan, modifier = Modifier.size(14.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "السياق النشط: $activeContextLabel"
                            } else {
                                "Active Study Context: $activeContextLabel"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Tutor Instruction Mode Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TutorInstructionMode.entries.forEach { mode ->
                        val active = mode == selectedMode
                        Surface(
                            modifier = Modifier
                                .clickable { onSelectMode(mode) }
                                .testTag("ai_mode_${mode.name}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (active) MedCyan else MedPanelNavy,
                            border = BorderStroke(1.dp, if (active) MedCyan else MedBorderHairline)
                        ) {
                            Text(
                                text = mode.label.get(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (active) MedObsidianNavy else Color.White,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Conversation Stream
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 960.dp)
                .testTag("ai_messages_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Educational Safety & Privacy Banner
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MedPanelNavy,
                    border = BorderStroke(1.dp, MedBorderHairline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (language == AppLanguage.ARABIC) {
                            "تنويه السلامة والخصوصية: المعلم الذكي أداة تعليمية طبية فقط ولا يقدم تشخيصاً شخصياً أو وصفات علاجية. تجنّب إدخال بيانات صحية شخصية حساسة."
                        } else {
                            "Educational Safety & Privacy: This AI Tutor supports medical education only and never diagnoses or prescribes. Please do not enter personal medical records or private patient data."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MedTextSecondary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (messages.isEmpty()) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (language == AppLanguage.ARABIC) "جرّب أحد هذه الأسئلة الطبية الموجهة:" else "Try a grounded medical study prompt:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MedCyan,
                            fontWeight = FontWeight.Bold
                        )
                        quickPrompts.forEachIndexed { idx, prompt ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isStreaming) { onSendQuestion(prompt) }
                                    .testTag("ai_quick_prompt_$idx"),
                                shape = RoundedCornerShape(14.dp),
                                color = MedPanelNavy,
                                border = BorderStroke(1.dp, MedCyan.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                TutorMessageBubble(
                    message = msg,
                    language = language,
                    onExecuteAction = onExecuteAction,
                    onRetry = onRetryLast
                )
            }
        }

        // Bottom Input Composer
        Surface(
            color = MedDeepNavy,
            border = BorderStroke(1.dp, MedBorderHairline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 960.dp)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_prompt_input"),
                    placeholder = {
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "اطرح سؤالاً طبياً تعليمياً (عربي أو English)..."
                            } else {
                                "Ask an educational medical question (EN / العربية)..."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MedTextSecondary
                        )
                    },
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank() && !isStreaming) {
                                val q = inputText
                                inputText = ""
                                onSendQuestion(q)
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedCyan,
                        unfocusedBorderColor = MedBorderHairline,
                        focusedContainerColor = MedObsidianNavy,
                        unfocusedContainerColor = MedObsidianNavy,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (inputText.isNotBlank() && !isStreaming) MedCyan else MedPanelNavy,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable(enabled = inputText.isNotBlank() && !isStreaming) {
                            val q = inputText
                            inputText = ""
                            onSendQuestion(q)
                        }
                        .testTag("ai_send_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isStreaming) {
                            CircularProgressIndicator(
                                color = MedCyan,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = if (language == AppLanguage.ARABIC) "إرسال" else "Send",
                                tint = if (inputText.isNotBlank()) MedObsidianNavy else MedTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TutorMessageBubble(
    message: TutorMessage,
    language: AppLanguage,
    onExecuteAction: (ValidatedTutorAction) -> Unit,
    onRetry: () -> Unit
) {
    val bgColor = when {
        message.isUser -> MedCyanContainer
        message.emergencyWarning -> MedAlertContainer
        else -> MedPanelNavy
    }
    val borderColor = when {
        message.isUser -> MedCyan.copy(alpha = 0.6f)
        message.emergencyWarning -> MedAlertRed
        message.errorMessage != null -> MedAlertRed.copy(alpha = 0.7f)
        else -> MedBorderHairline
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_msg_${message.id}"),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (message.isUser) {
                        if (language == AppLanguage.ARABIC) "أنت" else "You"
                    } else {
                        if (language == AppLanguage.ARABIC) "المعلم الطبي الذكي" else "MEDVERSE AI Tutor"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (message.isUser) MedCyan else MedTeal,
                    fontWeight = FontWeight.Bold
                )
                if (message.isStreaming) {
                    CircularProgressIndicator(
                        color = MedCyan,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (message.emergencyWarning) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MedAlertRed.copy(alpha = 0.22f),
                    border = BorderStroke(1.dp, MedAlertRed)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = MedAlertRed)
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "تنبيه طوارئ طبية: إذا كنت تمر بأعراض حادة أو مهددة للحياة، تواصل فوراً مع خدمات الإسعاف المحلية أو توجه لأقرب قسم طوارئ."
                            } else {
                                "Medical Emergency Alert: If you or someone nearby is experiencing acute or life-threatening symptoms, call local emergency services immediately."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (message.personalMedicalDisclaimer) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MedPendingContainer,
                    border = BorderStroke(1.dp, MedPendingAmber.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = MedPendingAmber, modifier = Modifier.size(18.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) {
                                "هذا الرد للتعليم العام فقط ولا يُعد تشخيصاً طبياً أو وصفة شخصية. استشر طبيباً مرخصاً لوضعك الصحي الخاص."
                            } else {
                                "General education only — not a personal diagnosis or prescription. Please consult a licensed clinician for personal care."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            if (message.text.isNotBlank()) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            if (message.errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MedAlertContainer,
                    border = BorderStroke(1.dp, MedAlertRed.copy(alpha = 0.65f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MedAlertRed, modifier = Modifier.size(18.dp))
                            Text(
                                text = message.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                        IconButton(onClick = onRetry, modifier = Modifier.testTag("ai_retry_button")) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = MedCyan)
                        }
                    }
                }
            }

            // Validated Cross-Module Navigation Action Chips
            if (message.validatedActions.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    message.validatedActions.forEach { action ->
                        Surface(
                            modifier = Modifier
                                .clickable { onExecuteAction(action) }
                                .testTag("ai_action_${action.type.name}_${action.targetId}"),
                            shape = RoundedCornerShape(10.dp),
                            color = MedCyan,
                            border = BorderStroke(1.dp, MedCyan)
                        ) {
                            Text(
                                text = action.label.get(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = MedObsidianNavy,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Verified Knowledge Base Citations
            if (message.citations.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (language == AppLanguage.ARABIC) "المراجع العلمية الموثقة في هذا الرد:" else "Verified Reference Grounding:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MedVerifiedGreen,
                        fontWeight = FontWeight.Bold
                    )
                    message.citations.distinctBy { it.id }.take(3).forEach { ref ->
                        Text(
                            text = "• ${ref.title} — ${ref.organization}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MedTextSecondary
                        )
                    }
                }
            }
        }
    }
}
