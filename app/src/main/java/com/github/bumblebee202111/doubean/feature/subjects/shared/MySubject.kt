package com.github.bumblebee202111.doubean.feature.subjects.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.bumblebee202111.doubean.R
import com.github.bumblebee202111.doubean.feature.subjects.MySubjectUiState
import com.github.bumblebee202111.doubean.model.SubjectType
import com.github.bumblebee202111.doubean.ui.LoginPromptText

fun LazyListScope.mySubject(
    mySubjectUiState: MySubjectUiState,
    onStatusClick: (userId: String, subjectType: SubjectType) -> Unit,
    onLoginClick: () -> Unit,
) {
    item {
        when (mySubjectUiState) {
            MySubjectUiState.Error -> {
                
            }

            MySubjectUiState.Loading -> {
                
            }

            MySubjectUiState.NotLoggedIn -> {
                LoginPromptText(
                    contentStringRes = R.string.login_prompt_to_check_my_subjects,
                    onLoginClick = onLoginClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            is MySubjectUiState.Success -> {
                val mySubject = mySubjectUiState.mySubject
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.title_my_subject, mySubject.name),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    IconButton(onClick = {
                        onStatusClick(mySubjectUiState.userId, mySubject.type)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.size(4.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mySubject.interests) {
                        OutlinedCard(onClick = {
                            onStatusClick(
                                mySubjectUiState.userId,
                                mySubject.type
                            )
                        }) {
                            Text(
                                text = "${it.count} ${it.title}",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}