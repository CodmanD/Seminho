package codman.seminho.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codman.seminho.Model.AlarmEvent;
import codman.seminho.PagesActivity;

/**
 * Created by DI1 on 21.03.2018.
 */

public class FirestoreHelper {

    private final String TAG="Seminho";
    private String user="";
    private Context context;
    FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();


    public FirestoreHelper(Context context, String user)
    {
        this.context=context;
        this.user=user;

    }

    public void addToFirestore(AlarmEvent ae)
    {
        //Log.d(TAG,"TO Firestore event ="+ae.getTitle());
        Map<String,Object> event = new HashMap<>();
        event.put("owner", user);
        event.put("title", ae.getTitle());
        event.put("category",ae.getCategory());
        event.put("description",ae.getContent());
        //event.put("uid",currentAE.getUID());
        event.put("lastModified",ae.getLastModified());
        event.put("startTime", ae.getStartTime());
        event.put("finishTime", ae.getFinishTime());

        mFirestore.collection("events").document(ae.getUID())

                .set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       Toast.makeText(context,"Save to Firestore",Toast.LENGTH_SHORT).show();
                        // Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Error saving to Firestore",Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void updateFirestore(AlarmEvent ae)
    {

        // Log.d(TAG,"Update Firestore");
        Map<String,Object> event = new HashMap<>();
        event.put("owner", user);
        event.put("title",ae.getTitle());
        event.put("category",ae.getCategory());
        event.put("description",ae.getContent());
        //event.put("uid",currentAE.getUID());
        event.put("lastModified",ae.getLastModified());
        event.put("startTime", ae.getStartTime());
        event.put("finishTime", ae.getFinishTime());

        mFirestore.collection("events").document(ae.getUID()).update(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // Log.d(TAG, "DocumentSnapshot successfully update!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w(TAG, "Error updating document", e);
                    }
                });
    }


    public ArrayList<AlarmEvent> readFromFirestore()
    {

// Remove the 'capital' field from the document

    final ArrayList<AlarmEvent> list= new ArrayList<>();
        Log.d(TAG,"read from server= ");
       // List listE=mFirestore.document("events");

        mFirestore.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();


                    if (document != null ) {

                        String title;
                        String uid;
                        String category;
                        String description;
                        long startTime;
                        long  finishTime;
                        long  lastModified;
                    for(int i=0;i<document.getDocuments().size();i++)
                    {

                        title=String.valueOf(document.getDocuments().get(i).getData().get("title"));
                        description=String.valueOf(document.getDocuments().get(i).getData().get("description"));
                        category=String.valueOf(document.getDocuments().get(i).getData().get("category"));
                        //title=String.valueOf(document.getDocuments().get(i).getData().get("title"));
                        uid=document.getDocuments().get(i).getId();
                        startTime=Long.parseLong(String.valueOf(document.getDocuments().get(i).getData().get("startTime")));
                        finishTime=Long.parseLong(String.valueOf(document.getDocuments().get(i).getData().get("finishTime")));
                        lastModified=Long.parseLong(String.valueOf(document.getDocuments().get(i).getData().get("lastModified")));

                        AlarmEvent ae= new AlarmEvent(uid,title,description,category,startTime,finishTime,lastModified);
                        list.add(ae);
                        Log.d(TAG, "Document data: " + document.getDocuments().get(i).getId());
                    }

                        //Log.d(TAG, "DocumentSnapshot data: " + document.toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });




        /*
        for(int i=0;i<listE.size();i++)
        {
            Log.d(TAG,"AE from server= "+listE.get(i).toString());
        }
**/
        // Log.d(TAG,"Update Firestore");
       /*
        Map<String,Object> event = new HashMap<>();
        event.put("owner", user);
        event.put("title",ae.getTitle());
        event.put("category",ae.getCategory());
        event.put("description",ae.getContent());
        //event.put("uid",currentAE.getUID());
        event.put("lastModified",ae.getLastModified());
        event.put("startTime", ae.getStartTime());
        event.put("finishTime", ae.getFinishTime());

        mFirestore.collection("events").document(ae.getUID()).update(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Log.d(TAG, "DocumentSnapshot successfully update!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error updating document", e);
                    }
                });
                */
       return list;
    }

    public void deleteFromFirestore(AlarmEvent ae)
    {
       // Log.d(TAG,"Delete event from Firestore");

        mFirestore.collection("events").document(ae.getUID()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(context,"DELETE FROM Firestore",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
