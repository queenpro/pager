/*
 * SOFTWARE BY FFS RELEASED UNDER AGPL LICENSE.
 * REFER TO WWW.FFS.IT AND INFO@FFS.IT FOR INFO.
 * Author: Franco Venezia
  
    Copyright (C) <2019>  <Franco Venezia @ ffs.it>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package models;

import REVOdbManager.EVOpagerParams;
import REVOdbManager.Settings;

/**
 *
 * @author Franco
 */
public class IncomingRequest {

    String outputStreamType;
    String remoteIP;
    String XRealIP;
    String XForwardedFor;
    String recorded;
    Boolean needsAuthentication;
    EVOpagerParams myParams; //vengono inviati con la richiesta POST
    Settings mySettings; // vengono definiti dal FRAME GAIA e valgono per tutte le istanze
    Object response;
    gate myGate;
    String afterProcessByObjectRoutines;
    int sessionValid;

    String responseType;
    String connectors;
    String params;
    String keyValue;
    String keyType;
    String PathInfo;
    String RequestURI;
    boolean apiServerRequest = false;

    public boolean isApiServerRequest() {
        return apiServerRequest;
    }

    public void setApiServerRequest(boolean apiServerRequest) {
        this.apiServerRequest = apiServerRequest;
    }

    public String getRequestURI() {
        return RequestURI;
    }

    public void setRequestURI(String RequestURI) {
        this.RequestURI = RequestURI;
    }

    public String getPathInfo() {
        return PathInfo;
    }

    public void setPathInfo(String PathInfo) {
        this.PathInfo = PathInfo;
    }

    public String getXForwardedFor() {
        return XForwardedFor;
    }

    public void setXForwardedFor(String XForwardedFor) {
        this.XForwardedFor = XForwardedFor;
    }

    public String getXRealIP() {
        return XRealIP;
    }

    public void setXRealIP(String XRealIP) {
        this.XRealIP = XRealIP;
    }

    public String getAfterProcessByObjectRoutines() {
        return afterProcessByObjectRoutines;
    }

    public void setAfterProcessByObjectRoutines(String afterProcessByObjectRoutines) {
        this.afterProcessByObjectRoutines = afterProcessByObjectRoutines;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getConnectors() {
        return connectors;
    }

    public void setConnectors(String connectors) {
        this.connectors = connectors;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public int getSessionValid() {
        return sessionValid;
    }

    public void setSessionValid(int sessionValid) {
        this.sessionValid = sessionValid;
    }

    public gate getMyGate() {
        return myGate;
    }

    public void setMyGate(gate myGate) {
        this.myGate = myGate;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getOutputStreamType() {
        return outputStreamType;
    }

    public void setOutputStreamType(String outputStreamType) {
        this.outputStreamType = outputStreamType;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getRecorded() {
        return recorded;
    }

    public void setRecorded(String recorded) {
        this.recorded = recorded;
    }

    public Boolean getNeedsAuthentication() {
        return needsAuthentication;
    }

    public void setNeedsAuthentication(Boolean needsAuthentication) {
        this.needsAuthentication = needsAuthentication;
    }

    public EVOpagerParams getMyParams() {
        return myParams;
    }

    public void setMyParams(EVOpagerParams myParams) {
        this.myParams = myParams;
    }

    public Settings getMySettings() {
        return mySettings;
    }

    public void setMySettings(Settings mySettings) {
        this.mySettings = mySettings;
    }

}
