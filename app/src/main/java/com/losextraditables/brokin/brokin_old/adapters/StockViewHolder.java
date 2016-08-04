package com.losextraditables.brokin.brokin_old.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.losextraditables.brokin.R;
import com.losextraditables.brokin.brokin_old.adapters.listeners.OnStockClickListener;
import com.losextraditables.brokin.brokin_old.db.DatabaseHelper;
import com.losextraditables.brokin.brokin_old.models.StockModel;
import com.losextraditables.brokin.brokin_old.models.UserModel;
import com.losextraditables.brokin.brokin_old.views.activity.MainTabbedActivity;
import com.losextraditables.brokin.re_brokin.android.view.activities.ShareInfoActivity;
import java.math.BigDecimal;
import java.sql.SQLException;

public class StockViewHolder extends RecyclerView.ViewHolder {

    public static final String USER_USERNAME = "user_username";
    private final OnStockClickListener onStockClickListener;
    private final String LOG_TAG = getClass().getSimpleName();

    @Bind(R.id.stock_author) TextView author;
    @Bind(R.id.stock_number_of_stocks) TextView name;
    @Bind(R.id.stock_value) TextView value;
    @Bind(R.id.stock_percent_change) TextView percent;
    Context context;

    public StockViewHolder(View itemView,
                           OnStockClickListener onStockClickListener, Context context) {
        super(itemView);
        this.onStockClickListener = onStockClickListener;
        this.context = context;
        ButterKnife.bind(this, itemView);
    }

    public void render(final StockModel stockModel) {
        this.setClickListener(stockModel);
        author.setText(stockModel.getSymbol());
        name.setText(stockModel.getName());
        BigDecimal valueTwoDecimals = round(stockModel.getValue(),2);

        value.setText("$" + String.valueOf(valueTwoDecimals));
        percent.setText("Change: " + String.valueOf(stockModel.getChangePercentage()+"%"));

        if (stockModel.getChangePercentage() < 0) {
            value.setTextColor(context.getResources().getColor(R.color.red));
        } else if (stockModel.getChangePercentage() > 0) {
            value.setTextColor(context.getResources().getColor(R.color.primary));
        } else {
            value.setTextColor(context.getResources().getColor(R.color.gray));
        }


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (prefs.getString(USER_USERNAME, null) == null) {
                    createUserAdapter(v);
                } else {
                    Intent callingIntent =
                        ShareInfoActivity.getCallingIntent(context, stockModel.getSymbol(), false);
                    context.startActivity(callingIntent);
                }
            }
        });

    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    private void createUserAdapter(final View v) {
        SweetAlertDialog dialog = new SweetAlertDialog(context);
        dialog
            .setTitleText("Create a user!")
            .setContentText("You haven't created a user yet. Please insert your name to continue:")
            .showEditText(true, null)
            .setConfirmText("Sign in")
            .setConfirmClickListener(sweetAlertDialog -> {
                String userNameString = dialog.getEditTextInput();
                String usernameWithoutSpaces = userNameString.replaceAll("\\s+", "");
                Toast.makeText(context, userNameString, Toast.LENGTH_SHORT).show();
                if(usernameWithoutSpaces.length() < 2) {
                    Toast.makeText(context, "Username must have two characters minimum", Toast.LENGTH_SHORT).show();
                } else {
                    UserModel userModel = new UserModel();
                    createUserInfo(userModel, userNameString);
                    updateUserInfoInToolbar(userModel);
                    insertUserInfoInDB(userModel);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor prefEditor = prefs.edit();
                    prefEditor.putString(USER_USERNAME, userModel.getUserName());
                    prefEditor.apply();
                    dialog.setTitleText("Welcome!")
                        .setContentText("Now you can trade freely")
                        .setConfirmText("OK")
                        .showEditText(false, InputType.TYPE_CLASS_NUMBER)
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                }
            })
            .setCancelText("Cancel")
            .show();
    }

    private void insertUserInfoInDB(UserModel userModel) {
        DatabaseHelper helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        Dao dao;
        try {
            dao = helper.getUserModelDao();
            dao.create(userModel);
            Log.e(LOG_TAG, "Creado usuario");
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Error creando usuario");
        }
        OpenHelperManager.releaseHelper();
    }

    private void createUserInfo(UserModel userModel, String username) {
        if(username.isEmpty()) {
            userModel.setUserName("user"+String.valueOf(Math.random()));
        } else {
            userModel.setUserName(username);
        }
        userModel.setCash(10000.0F);
    }

    private void updateUserInfoInToolbar(UserModel userModel) {
        ((MainTabbedActivity) context).getSupportActionBar().setTitle(userModel.getUserName());
        ((MainTabbedActivity) context).getSupportActionBar().setSubtitle(userModel.getCash().toString() + "$");
    }

    private void setClickListener(final StockModel stockModel) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onStockClickListener.onStockClick(stockModel);
            }
        });
    }
}
