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
import androidx.appcompat.app.AppCompatActivity;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import sukriti.ngo.mis.databinding.ConfigureUserVerifyCommunicationBinding;
import sukriti.ngo.mis.utils.AuthenticationClient;
import sukriti.ngo.mis.utils.UserAlertClient;

public class VerifyCommunicationDetails extends AppCompatActivity {
    private ConfigureUserVerifyCommunicationBinding binding;
    private UserAlertClient userAlertClient;
    private int verificationType = -1;
    private int status = -1;

    private final int STATUS_DEFAULT = 1;
    private final int STATUS_REQUESTED = 2;
    private final int TYPE_EMAIL = 1;
    private final int TYPE_NUMBER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ConfigureUserVerifyCommunicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(status == STATUS_DEFAULT){
            binding.details.setVisibility(View.VISIBLE);
            binding.verification.setVisibility(View.GONE);
        }else {
            binding.verification.setVisibility(View.VISIBLE);
            binding.details.setVisibility(View.GONE);
        }
    }

    private void init(){
        verificationType = getIntent().getIntExtra("type",-1);
        binding.id.setText(getIntent().getStringExtra("display"));
        status = STATUS_DEFAULT;
        userAlertClient = new UserAlertClient(this);
        binding.toolbar.mainToolbarTitle.setText("Verify Email");
        setSupportActionBar(binding.toolbar.mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.toolbar.mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit(false);
            }
        });

        binding.sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAlertClient.showWaitDialog("Requesting verification code...");

                AuthenticationClient.getPool().getUser(AuthenticationClient.getCurrUser())
                        .getAttributeVerificationCodeInBackground(verificationType == TYPE_EMAIL ? "email":"phone_number", sendCodeRequestHandler);
            }
        });

        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateCodeFormat()){
                    userAlertClient.showWaitDialog("Validating verification code...");
                    AuthenticationClient.getPool().getUser(AuthenticationClient.getCurrUser())
                            .verifyAttributeInBackground(verificationType == TYPE_EMAIL ? "email":"phone_number",
                                    binding.code.getText().toString(), verifyCodeRequestHandler);
                }
            }
        });
    }

    private VerificationHandler sendCodeRequestHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            userAlertClient.closeWaitDialog();
            status = STATUS_REQUESTED;
            binding.verification.setVisibility(View.VISIBLE);
            binding.details.setVisibility(View.GONE);
            binding.code.requestFocus();
            userAlertClient.showDialogMessage("Verification code sent",
                    "Code was sent to "+cognitoUserCodeDeliveryDetails.getDestination()+" via "+cognitoUserCodeDeliveryDetails.getDeliveryMedium(),
                    false);
        }

        @Override
        public void onFailure(Exception exception) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Verification code request failed!",exception.toString(),false);
        }
    };

    GenericHandler verifyCodeRequestHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            userAlertClient.closeWaitDialog();
            exit(true);
        }

        @Override
        public void onFailure(Exception exception) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Verification failed", AuthenticationClient.formatException(exception),false);
        }
    };

    private boolean validateCodeFormat() {
        String code = binding.code.getText().toString();
        if(code == null) {
            binding.message.setText("Please enter the validation code received");
            return false;
        }

        if(code.length() < 1) {
            binding.message.setText("Please enter the validation code received");
            return false;
        }

        return true;
    }

    private void exit(boolean verified) {
        Intent intent = new Intent();
        intent.putExtra("verified",verified);
        intent.putExtra("type",verificationType);
        setResult(RESULT_OK, intent);
        finish();
    }
}
