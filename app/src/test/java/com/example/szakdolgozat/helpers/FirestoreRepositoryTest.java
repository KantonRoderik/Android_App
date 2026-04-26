package com.example.szakdolgozat.helpers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FirestoreRepositoryTest {

    @Mock
    private FirebaseFirestore mockDb;
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockUser;
    @Mock
    private DocumentReference mockDocRef;
    @Mock
    private CollectionReference mockCollRef;

    private FirestoreRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Reset the singleton instance to null before creating a new one for testing
        // to avoid calling the real Firebase.getInstance()
        FirestoreRepository.setInstance(null);
        
        // Create the repository with mocked dependencies
        repository = new FirestoreRepository(mockDb, mockAuth);
        FirestoreRepository.setInstance(repository);

        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("test_uid");
        when(mockDb.collection("users")).thenReturn(mockCollRef);
        when(mockCollRef.document("test_uid")).thenReturn(mockDocRef);
    }

    @Test
    public void getUserData_CallsCorrectDocument() {
        // When
        repository.getUserData();

        // Then
        verify(mockDb).collection("users");
        verify(mockCollRef).document("test_uid");
        verify(mockDocRef).get();
    }
}
