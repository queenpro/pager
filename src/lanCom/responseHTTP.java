/*
 * Copyright (C) 2022 Franco Venezia @ www.ffs.it
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lanCom;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class responseHTTP {
    int ResponseCode;
    String responseString;

    public int getResponseCode() {
        return ResponseCode;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseCode(int ResponseCode) {
        this.ResponseCode = ResponseCode;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
    
}
