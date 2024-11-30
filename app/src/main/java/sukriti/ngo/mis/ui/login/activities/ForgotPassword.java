/*
 * Copyright 2013-2017 Amazon.com,
 * Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the
 * License. A copy of the License is located at
 *
 *      http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, express or implied. See the License
 * for the specific language governing permissions and
 * limitations under the License.
 */

package sukriti.ngo.mis.ui.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;

import sukriti.ngo.mis.databinding.ConfigureUserVerifyCommunicationBinding;
import sukriti.ngo.mis.databinding.ForgotPasswordBinding;
import sukriti.ngo.mis.utils.AuthenticationClient;
import sukriti.ngo.mis.utils.UserAlertClient;
import sukriti.ngo.mis.utils.Utilities;

public class ForgotPassword extends AppCompatActivity {
    private ForgotPasswordBinding binding;
    private UserAlertClient userAlertClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {

        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if (extras.containsKey("destination")) {
                String dest = extras.getString("destination");
                String delMed = extras.getString("deliveryMed");
                binding.title.setText("Code to set a new password was sent to " + dest + " via "+delMed);
            }
        }

        userAlertClient = new UserAlertClient(this);
        binding.toolbar.mainToolbarTitle.setText("Forgot password");
        setSupportActionBar(binding.toolbar.mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.toolbar.mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit("","",RESULT_CANCELED);
            }
        });

        binding.changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeypad(getApplicationContext(),binding.code);
                userAlertClient.showWaitDialog("Validating request...");
                if(validateInputs())
                    exit(binding.password.getText().toString(), binding.code.getText().toString(),RESULT_OK);
            }
        });

    }


    private boolean validateInputs() {
        String code = binding.code.getText().toString();
        if (code.length() < 1) {
            binding.codeErr.setText("Please enter the validation code received");
            return false;
        }

        String password = binding.password.getText().toString();
        if (password.length() < 6) {
            binding.codeErr.setText("Password Invalid. Please enter a password with minimum 6 characters.");
            return false;
        }

        return true;
    }

    private void exit(String newPass, String code,int status) {
        Intent intent = new Intent();
        if(newPass == null || code == null) {
            newPass = "";
            code = "";
        }
        intent.putExtra("newPass", newPass);
        intent.putExtra("code", code);
        setResult(status, intent);
        finish();
    }
}
