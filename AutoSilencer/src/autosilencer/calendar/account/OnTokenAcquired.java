package uk.co.rm.android.AutoSilencer.acc;

import java.io.IOException;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;

public class OnTokenAcquired implements AccountManagerCallback<Bundle> {

	private static String token;
	
	public void run(AccountManagerFuture<Bundle> result) {
		// Get the result of the operation from the AccountManagerFuture.
//	        Bundle bundle = null;
//			try {
//				bundle = result.getResult();
//			} catch (OperationCanceledException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (AuthenticatorException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    
//        // The token is a named value in the bundle. The name of the value
//        // is stored in the constant AccountManager.KEY_AUTHTOKEN.
//        token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
        
		
	}
}
