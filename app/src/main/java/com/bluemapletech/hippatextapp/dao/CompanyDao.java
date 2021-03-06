package com.bluemapletech.hippatextapp.dao;

import android.net.Uri;
import android.util.Log;

import com.bluemapletech.hippatextapp.model.User;
import com.bluemapletech.hippatextapp.utils.MailSender;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Kumaresan on 20-10-2016.
 */

public class CompanyDao {

    private static final String TAG = CompanyDao.class.getCanonicalName();

    private FirebaseDatabase firebaseDatabaseRef;

    private DatabaseReference databaseRef;

    public boolean acceptedCompany(User user){
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        String rearrangeCompanyName = user.getCompanyName().replace(".","-");
        DatabaseReference databaseRefs = firebaseDatabaseRef.getReference().child("approvedCompany").child(rearrangeCompanyName).child("companyName");
        databaseRefs.setValue(user.getCompanyName());
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue(user.getAuth());
        try {
            String acceptEmail = user.getUserName().replace("-", ".");
            MailSender runners = new MailSender();
            String value = "This email is to notify you that your profile has been accepted by HippaText.\n" +
                    "Thanks for showing interest.";
            runners.execute("Profile has been accepted!",value,"hipaatext123@gmail.com",acceptEmail);

        } catch (Exception ex) {
        }
        return true;
    }

    public boolean pendingCompany(User user) {
        Log.d(TAG, "Add invited company dao method has been called!");
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue(user.getAuth());
        return true;
    }

    public boolean deleteCompany(User user) {
        Log.d(TAG, "Add invited company dao method has been called!");
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue(user.getAuth());
        try {
            String acceptEmail = user.getUserName().replace("-", ".");
            MailSender runners = new MailSender();
            String value = "This email is to notify you that your profile has been rejected by HippaText.\n" +
                    "Thanks for showing interest.";
            runners.execute("Profile has been rejected!",value,"hipaatext123@gmail.com",acceptEmail);

        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
        }
        return true;
    }
    public boolean deleteCompanys(User user) {
        Log.d(TAG, "Add invited company dao method has been called!");
        String reArrangeEmail = user.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
        databaseRef.setValue(user.getAuth());
        try {
            String acceptEmail = user.getUserName().replace("-", ".");
            MailSender runners = new MailSender();
            String value = "This email is to notify you that your profile has been rejected by HippaText.\n" +
                    "Thanks for showing interest.";
            runners.execute("Profile has been rejected!",value,"hipaatext123@gmail.com",acceptEmail);

        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
        }
        return true;
    }
    public boolean deleteCompanyAdminAndUser(List<User> emailId) {
        Log.d(TAG, "Add invited company dao method has been called!");

        for(int i=0;i<emailId.size();i++){
            String reArrangeEmail = emailId.get(i).getUserName().replace(".", "-");
            firebaseDatabaseRef = FirebaseDatabase.getInstance();
            DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("auth");
            databaseRef.setValue("3");
            if(emailId.get(i).getRole().matches("admin")){
                String rearrangeCompanyName = emailId.get(i).getCompanyName().replace(".","-");
                DatabaseReference databaseRefs = firebaseDatabaseRef.getReference().child("approvedCompany").child(rearrangeCompanyName);
                databaseRefs.removeValue();
            }

            try {
                String acceptEmail = emailId.get(i).getUserName().replace("-", ".");
                MailSender runners = new MailSender();
                String value = "This email is to notify you that your profile has been rejected by HippaText.\n" +
                        "Thanks for showing interest.";
                runners.execute("Profile has been rejected!",value,"hipaatext123@gmail.com",acceptEmail);

            } catch (Exception ex) {
                // Toast.makeText(getApplicationContext(), ex.toString(), 100).show();
            }
        }



        return true;
    }
    public boolean profileImageUrl(User comInfos) {
        Log.d(TAG, "Add profileImage url!");
        String reArrangeEmail = comInfos.getUserName().replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("profilePhoto");
        databaseRef.setValue(comInfos.getProfilePjhoto());
        return true;
    }

    public void profileImageUrl(String profilePjhoto, String userName) {
        Log.d(TAG, "Add profileImage url!");
        String reArrangeEmail = userName.replace(".", "-");
        firebaseDatabaseRef = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = firebaseDatabaseRef.getReference().child("userDetails").child(reArrangeEmail).child("profilePhoto");
        databaseRef.setValue(profilePjhoto);

    }
}
