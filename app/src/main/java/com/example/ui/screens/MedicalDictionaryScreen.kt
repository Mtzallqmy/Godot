package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.i18n.AppLanguage
import com.example.core.i18n.UiStrings
import com.example.core.model.AnatomicalStructure
import com.example.core.model.DictionaryCategory
import com.example.core.model.MedicalTerm
import com.example.ui.components.ReferenceSourceCard
import com.example.ui.components.ReviewStatusBadge
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.MedBorderHairline
import com.example.ui.theme.MedCyan
import com.example.ui.theme.MedCyanContainer
import com.example.ui.theme.MedDeepNavy
import com.example.ui.theme.MedObsidianNavy
import com.example.ui.theme.MedPanelNavy
import com.example.ui.theme.MedPendingAmber
import com.example.ui.theme.MedTeal
import com.example.ui.theme.MedTextSecondary
import com.example.ui.viewmodel.MedVerseUiState

@Composable
fun MedicalDictionaryScreen(
    state: MedVerseUiState,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (DictionaryCategory) -> Unit,
    onToggleBookmarkedOnly: () -> Unit,
    onSelectTerm: (String?) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onOpenAnatomyIn3D: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val language = state.language

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isTwoPane = maxWidth >= 780.dp

        if (isTwoPane) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DictionaryListPane(
                    state = state,
                    onQueryChange = onQueryChange,
                    onCategorySelected = onCategorySelected,
                    onToggleBookmarkedOnly = onToggleBookmarkedOnly,
                    onSelectTerm = { onSelectTerm(it) },
                    onToggleBookmark = onToggleBookmark,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                val activeTerm = state.selectedTerm ?: state.filteredTerms.firstOrNull()
                if (activeTerm != null) {
                    DictionaryTermDetailPane(
                        term = activeTerm,
                        isBookmarked = activeTerm.id in state.bookmarkedTermIds,
                        allStructures = state.structures,
                        language = language,
                        showBackButton = false,
                        onBack = {},
                        onToggleBookmark = { onToggleBookmark(activeTerm.id) },
                        onOpenAnatomyIn3D = onOpenAnatomyIn3D,
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                    )
                }
            }
        } else {
            val activeTerm = state.selectedTerm
            if (activeTerm != null) {
                BackHandler { onSelectTerm(null) }
                DictionaryTermDetailPane(
                    term = activeTerm,
                    isBookmarked = activeTerm.id in state.bookmarkedTermIds,
                    allStructures = state.structures,
                    language = language,
                    showBackButton = true,
                    onBack = { onSelectTerm(null) },
                    onToggleBookmark = { onToggleBookmark(activeTerm.id) },
                    onOpenAnatomyIn3D = onOpenAnatomyIn3D,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                )
            } else {
                DictionaryListPane(
                    state = state,
                    onQueryChange = onQueryChange,
                    onCategorySelected = onCategorySelected,
                    onToggleBookmarkedOnly = onToggleBookmarkedOnly,
                    onSelectTerm = { onSelectTerm(it) },
                    onToggleBookmark = onToggleBookmark,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                )
            }
        }
    }
}

@Composable
private fun DictionaryListPane(
    state: MedVerseUiState,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (DictionaryCategory) -> Unit,
    onToggleBookmarkedOnly: () -> Unit,
    onSelectTerm: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val language = state.language
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = UiStrings.dictTitle.get(language),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = UiStrings.dictSubtitle.get(language),
            style = MaterialTheme.typography.bodySmall,
            color = MedTextSecondary
        )

        OutlinedTextField(
            value = state.dictionaryQuery,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dictionary_search_input"),
            placeholder = {
                Text(
                    text = UiStrings.dictSearchHint.get(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedTextSecondary
                )
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MedCyan) },
            trailingIcon = {
                if (state.dictionaryQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search", tint = MedTextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MedCyan,
                unfocusedBorderColor = MedBorderHairline,
                focusedContainerColor = MedDeepNavy,
                unfocusedContainerColor = MedDeepNavy,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Category + Saved Bookmark Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier
                    .clickable(onClick = onToggleBookmarkedOnly)
                    .testTag("dictionary_bookmarks_filter"),
                shape = RoundedCornerShape(10.dp),
                color = if (state.bookmarkedOnly) MedPendingAmber else MedPanelNavy,
                border = BorderStroke(1.dp, if (state.bookmarkedOnly) MedPendingAmber else MedBorderHairline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = if (state.bookmarkedOnly) MedObsidianNavy else MedPendingAmber,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "${UiStrings.dictBookmarksOnly.get(language)} (${state.bookmarkedTermIds.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (state.bookmarkedOnly) MedObsidianNavy else Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            DictionaryCategory.entries.forEach { cat ->
                val selected = state.selectedCategory == cat
                Surface(
                    modifier = Modifier
                        .clickable { onCategorySelected(cat) }
                        .testTag("dict_cat_${cat.name.lowercase()}"),
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) MedCyan else MedPanelNavy,
                    border = BorderStroke(1.dp, if (selected) MedCyan else MedBorderHairline)
                ) {
                    Text(
                        text = cat.label.get(language),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) MedObsidianNavy else Color.White,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                    )
                }
            }
        }

        if (state.filteredTerms.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MedPanelNavy,
                border = BorderStroke(1.dp, MedBorderHairline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = UiStrings.dictNoResults.get(language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MedTextSecondary,
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.filteredTerms, key = { it.id }) { term ->
                    val isBookmarked = term.id in state.bookmarkedTermIds
                    val isSelected = term.id == state.selectedTermId
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectTerm(term.id) }
                            .testTag("dict_term_${term.id}"),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MedCyanContainer else MedPanelNavy,
                        border = BorderStroke(1.dp, if (isSelected) MedCyan else MedBorderHairline)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) term.arabicName else term.englishName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) term.englishName else term.arabicName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MedCyan
                                    )
                                }
                                IconButton(
                                    onClick = { onToggleBookmark(term.id) },
                                    modifier = Modifier.testTag("bookmark_btn_${term.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark term",
                                        tint = if (isBookmarked) MedPendingAmber else MedTextSecondary
                                    )
                                }
                            }
                            Text(
                                text = if (language == AppLanguage.ARABIC) term.definitionArabic else term.definitionEnglish,
                                style = MaterialTheme.typography.bodySmall,
                                color = MedTextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MedDeepNavy
                                ) {
                                    Text(
                                        text = term.category.label.get(language),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MedTeal,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = term.pronunciationIpa,
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = JetBrainsMonoFamily),
                                    color = MedTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DictionaryTermDetailPane(
    term: MedicalTerm,
    isBookmarked: Boolean,
    allStructures: List<AnatomicalStructure>,
    language: AppLanguage,
    showBackButton: Boolean,
    onBack: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenAnatomyIn3D: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.testTag("dictionary_term_detail"),
        shape = RoundedCornerShape(20.dp),
        color = MedPanelNavy,
        border = BorderStroke(1.dp, MedBorderHairline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (showBackButton) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("dict_detail_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to terms",
                                tint = MedCyan
                            )
                        }
                    }
                    ReviewStatusBadge(status = term.reviewStatus, language = language)
                }

                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Toggle Bookmark",
                        tint = if (isBookmarked) MedPendingAmber else MedTextSecondary
                    )
                }
            }

            // Simultaneous Bilingual Display
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = term.englishName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = term.arabicName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MedCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            // Pronunciation Architecture Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MedDeepNavy,
                border = BorderStroke(1.dp, MedBorderHairline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = UiStrings.dictPronunciation.get(language),
                        tint = MedTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = UiStrings.dictPronunciation.get(language),
                            style = MaterialTheme.typography.labelMedium,
                            color = MedTeal
                        )
                        Text(
                            text = "${term.pronunciationIpa}  •  ${term.pronunciationPhonetic}",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = JetBrainsMonoFamily),
                            color = Color.White
                        )
                    }
                }
            }

            // Bilingual Definitions
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MedDeepNavy,
                border = BorderStroke(1.dp, MedBorderHairline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.ARABIC) "التعريف الطبي (العربية)" else "Medical Definition (English)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MedCyan
                    )
                    Text(
                        text = if (language == AppLanguage.ARABIC) term.definitionArabic else term.definitionEnglish,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    HorizontalDivider(color = MedBorderHairline)
                    Text(
                        text = if (language == AppLanguage.ARABIC) "English Definition" else "التعريف باللغة العربية",
                        style = MaterialTheme.typography.labelMedium,
                        color = MedTeal
                    )
                    Text(
                        text = if (language == AppLanguage.ARABIC) term.definitionEnglish else term.definitionArabic,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MedTextSecondary
                    )
                }
            }

            // Synonyms
            val synonyms = term.synonymsEn + term.synonymsAr
            if (synonyms.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = UiStrings.dictSynonyms.get(language),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        synonyms.forEach { syn ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MedObsidianNavy,
                                border = BorderStroke(1.dp, MedBorderHairline)
                            ) {
                                Text(
                                    text = syn,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MedTextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Linked 3D Anatomical Structures
            val linkedStructures = term.relatedAnatomyIds.mapNotNull { id ->
                allStructures.firstOrNull { it.id == id }
            }
            if (linkedStructures.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = UiStrings.dictRelatedAnatomy.get(language),
                        style = MaterialTheme.typography.titleSmall,
                        color = MedCyan,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        linkedStructures.forEach { st ->
                            Surface(
                                modifier = Modifier
                                    .clickable { onOpenAnatomyIn3D(st.id) }
                                    .testTag("dict_open_3d_${st.id}"),
                                shape = RoundedCornerShape(10.dp),
                                color = MedCyanContainer,
                                border = BorderStroke(1.dp, MedCyan)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ViewInAr,
                                        contentDescription = null,
                                        tint = MedCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (language == AppLanguage.ARABIC) st.arabicName else st.englishName,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sources
            HorizontalDivider(color = MedBorderHairline)
            Text(
                text = UiStrings.labelSources.get(language),
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            term.sources.forEach { src ->
                ReferenceSourceCard(source = src, language = language)
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
