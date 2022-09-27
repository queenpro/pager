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
package REVOwebsocketManager;

import REVOdbManager.EVOpagerParams;

/**
 *
 * @author Franco Venezia @ www.ffs.it
 */
public class WShandlerInstance {
    /*
    Ogni volta che uso WShandler iso una unica istanza statica ma cambia il valore dei myParams che contengono la destinazione dei messaggi.
    Per questo l'oggetto WShandlerInstance si porta dietro l'informazione che viene così passata elle routine insieme al WShandler
    */
    WShandler myHandler;
    EVOpagerParams myParams;

    public WShandlerInstance(WShandler myHandler, EVOpagerParams myParams) {
        this.myHandler = myHandler;
        this.myParams = myParams;
    }

    public WShandler getMyHandler() {
        return myHandler;
    }

    public void setMyHandler(WShandler myHandler) {
        this.myHandler = myHandler;
    }

    public EVOpagerParams getMyParams() {
        return myParams;
    }

    public void setMyParams(EVOpagerParams myParams) {
        this.myParams = myParams;
    }
    
    
}
