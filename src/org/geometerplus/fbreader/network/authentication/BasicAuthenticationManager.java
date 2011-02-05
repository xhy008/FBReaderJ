/*
 * Copyright (C) 2010-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.network.authentication;

import java.util.*;

import org.geometerplus.zlibrary.core.options.ZLStringOption;
import org.geometerplus.zlibrary.core.network.ZLNetworkException;

import org.geometerplus.fbreader.network.*;

import org.geometerplus.fbreader.network.authentication.litres.LitResAuthenticationManager;

public class BasicAuthenticationManager extends NetworkAuthenticationManager {
	BasicAuthenticationManager(INetworkLink link, String sslCertificate) {
		super(link, sslCertificate);
	}

	/*
	 * Common manager methods
	 */
	public boolean isAuthorised(boolean useNetwork /* = true */) throws ZLNetworkException {
		// TODO: implement
		return false;
	}

	public void authorise(String password) throws ZLNetworkException {
		String url = Link.getLink(INetworkLink.URL_SIGN_IN);
		if (url == null) {
			throw new ZLNetworkException(NetworkException.ERROR_UNSUPPORTED_OPERATION);
		}
		// TODO: implement
	}

	public void logOut() {
		// TODO: implement
	}

	public BookReference downloadReference(NetworkBookItem book) {
		// TODO: implement
		return null;
	}

	public Map<String,String> getSmsRefillingData() {
		return Collections.emptyMap();
	}

	/*
	 * Account specific methods (can be called only if authorised!!!)
	 */
	public String currentUserName() {
		return null;
	}

	public boolean needsInitialization() {
		return false;
	}

	public void initialize() throws ZLNetworkException {
		throw new ZLNetworkException(NetworkException.ERROR_UNSUPPORTED_OPERATION);
	}

	// returns true if link must be purchased before downloading
	public boolean needPurchase(NetworkBookItem book) {
		return true;
	}

	public void purchaseBook(NetworkBookItem book) throws ZLNetworkException {
		throw new ZLNetworkException(NetworkException.ERROR_UNSUPPORTED_OPERATION);
	}

	public String currentAccount() {
		return null;
	}

	//public abstract ZLNetworkSSLCertificate certificate();

	/*
	 * refill account
	 */

	public void initUser(String userName, String sid) throws ZLNetworkException {
	}
}
