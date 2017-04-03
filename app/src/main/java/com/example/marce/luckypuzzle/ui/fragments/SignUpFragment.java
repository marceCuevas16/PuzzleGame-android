package com.example.marce.luckypuzzle.ui.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marce.luckypuzzle.R;
import com.example.marce.luckypuzzle.common.LuckyFragment;
import com.example.marce.luckypuzzle.di.component.DaggerSignUpComponent;
import com.example.marce.luckypuzzle.di.module.SignUpModule;
import com.example.marce.luckypuzzle.presenter.SignUpPresenterImp;
import com.example.marce.luckypuzzle.ui.activities.LaunchActivity;
import com.example.marce.luckypuzzle.ui.adapters.TextWatcherAdapter;
import com.example.marce.luckypuzzle.ui.viewModel.SignUpOptions;
import com.example.marce.luckypuzzle.ui.viewModel.SignUpView;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by marce on 26/03/17.
 */

public class SignUpFragment extends LuckyFragment implements SignUpView,View.OnClickListener{

    @Inject SignUpPresenterImp mPresenter;
    private Animation animShake;
    private ProgressDialog progressDialog;
    private android.support.v7.app.AlertDialog alertDialog;
    private TextView loginNow;
    private Button signUp;
    private EditText userName,email,password;
    private TextInputLayout userNameLayout,passwordLayout,emailLayout;
    private CircleImageView profilePicture;
    private CharSequence pictureOpions[];
    private boolean validUserName,validPassword,validEmail=true;
    private static final int TAKE_A_PHOTO=0,SELECT_FROM=1,RESULT_LOAD_IMG = 1;
    private static final int CAMERA_REQUEST = 1888;
    String imgDecodableString;

    @Override
    public void setUpComponent() {
        DaggerSignUpComponent.builder()
                .launchComponent(((LaunchActivity)getActivity()).getComponent())
                .signUpModule(new SignUpModule(this))
                .build().inject(this);
    }

    @Override
    protected int layout() {
        return R.layout.lucky_code_sign_up;
    }

    @Override
    protected void setUi(View v) {
        loginNow= (TextView) v.findViewById(R.id.login_now);
        signUp=(Button)v.findViewById(R.id.sign_up);
        profilePicture= (CircleImageView) v.findViewById(R.id.profilePicture);
        userName= (EditText) v.findViewById(R.id.userName);
        email= (EditText) v.findViewById(R.id.email);
        password= (EditText) v.findViewById(R.id.password);
        userNameLayout= (TextInputLayout) v.findViewById(R.id.userName_layout);
        emailLayout= (TextInputLayout) v.findViewById(R.id.email_layout);
        passwordLayout= (TextInputLayout) v.findViewById(R.id.password_layout);
    }

    @Override
    protected void init() {
        pictureOpions= new CharSequence[] {getString(R.string.takeAPhoto),
                getString(R.string.selectPhoto)};
        progressDialog= new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.pleaseWait));
        alertDialog= new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.sigupStatus));
        animShake = AnimationUtils.loadAnimation(getActivity(),R.anim.shake);
    }

    @Override
    protected void populate() {

    }

    @Override
    protected void setListeners() {
        loginNow.setOnClickListener(this);
        signUp.setOnClickListener(this);
        profilePicture.setOnClickListener(this);
        userName.addTextChangedListener(new TextWatcherAdapter(userName) {
            @Override
            public void validate(EditText editText, String text) {
                mPresenter.validateUserName(userName.getText().toString());
            }
        });
        password.addTextChangedListener(new TextWatcherAdapter(password) {
            @Override
            public void validate(EditText editText, String text) {
                mPresenter.validatePassword(password.getText().toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profilePicture:
               showPictureDialog();
                break;
            case R.id.sign_up:
                mPresenter.signUp(userName.getText().toString(),email.getText().toString(),
                        password.getText().toString());
                break;
            case R.id.sign_in:
                backToSignIn();
                break;
        }
    }

    @Override
    public void showPictureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.chooseAPhoto));
        builder.setItems(pictureOpions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case TAKE_A_PHOTO:
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        break;
                    case SELECT_FROM:
                        loadPictureFromGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void loadPictureFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void showProgress() {
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.hide();
    }

    @Override
    public void cancelProgress() {

    }

    @Override
    public void setEmptyUserNameError() {
        userName.setError(getString(R.string.emptyUserName));
        userName.setAnimation(animShake);
        userName.startAnimation(animShake);
        requestFocus(userNameLayout);
    }

    @Override
    public void setEmptyPasswordError() {
        password.setError(getString(R.string.emptyPassword));
        password.setAnimation(animShake);
        password.startAnimation(animShake);
        requestFocus(passwordLayout);
    }

    @Override
    public void setEmptyEmailError() {
        password.setError(getString(R.string.emptyEmail));
        password.setAnimation(animShake);
        password.startAnimation(animShake);
        requestFocus(passwordLayout);
    }

    @Override
    public void setValidUserName(boolean validUserName) {
        this.validUserName=validUserName;
    }

    @Override
    public void setValidEmail(boolean validEmail) {
        this.validEmail=validEmail;
    }

    @Override
    public void setSuccessSignUp() {
        Toast.makeText(getActivity(),"SIGN UP SUCCESSFUL",Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean isUserNameValid() {
        return validUserName;
    }

    @Override
    public boolean isEmailValid() {
        return validEmail;
    }

    @Override
    public void setInvalidUserNameError() {
        userName.setError(getString(R.string.shortUsername));
    }

    @Override
    public void setInvalidPasswordError() {
        password.setError(getString(R.string.shortPassword));
    }

    @Override
    public void setInvalidEmailError() {
        email.setError(getString(R.string.invalidEmail));
        email.setAnimation(animShake);
        email.startAnimation(animShake);
        requestFocus(emailLayout);
    }

    @Override
    public void setUserAlreadyExistsError() {
        alertDialog.setMessage(getString(R.string.invalidUsername));
        alertDialog.show();
    }

    @Override
    public void backToSignIn() {
        ((LaunchActivity)getActivity()).replaceFragmentWithBackStackAnimation(SignUpOptionsFragment.class,false);
    }

    @Override
    public void setUnknownError() {

    }

    @Override
    public void setValidPassword(boolean validPassword) {
        this.validPassword=validPassword;
    }

    @Override
    public boolean isPasswordValid() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == getActivity().RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                // Set the Image in ImageView after decoding the String
                profilePicture.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        if (requestCode == CAMERA_REQUEST && resultCode == getActivity().RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profilePicture.setImageBitmap(photo);
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
