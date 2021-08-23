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


package smartCore;

/**
 *
 * @author FFS INFORMATICA [info at ffs.it]
 */
public class smartObjRight {
     

        public int level;
        public int totalRight;
        public int canView;//1
        public int canModify;//2
        public int canDelete;//4
        public int canCreate;//8
        public int canPushButton;//16
        public int canEverything;//128
        public String Type; // se i diritti sono in una riga di add lo devo sapere per modificarli di conseguenza

        public void print() {
            System.out.println("level*********" + this.level);
            System.out.println("1.canView***************" + this.canView);
            System.out.println("2.canModify*************" + this.canModify);
            System.out.println("4.canDelete*************" + this.canDelete);
            System.out.println("8.canCreate*************" + this.canCreate);
            System.out.println("16.canPushButton*********" + this.canPushButton);
            System.out.println("128.canEverything*********" + this.canEverything);
        }

        public smartObjRight(int totalRight) {
            this.totalRight = totalRight;
            //System.out.println("ANALIZZO RIGHTS:" + totalRight);
            evaluateRights();

        }

        public void evaluateRights() {
            int tr = totalRight;
            if (tr < 0) {
                canEverything = -1;
                canPushButton = -1;
                canCreate = -1;
                canDelete = -1;
                canModify = -1;
                canView = -1;

                return;
            }
            if ((tr - 128) >= 0) {
                canEverything = 1;
                tr = tr - 128;
                //     System.out.println("  RIGHTS residui :" + tr);
            } else {
                canEverything = 0;
            }
            if ((tr - 16) >= 0) {
                canPushButton = 1;
                tr = tr - 16;
                //      System.out.println("  RIGHTS residui :" + tr);
            } else {
                canPushButton = 0;
            }
            if ((tr - 8) >= 0) {
                canCreate = 1;
                tr = tr - 8;
                //     System.out.println("  RIGHTS residui :" + tr);
            } else {
                canCreate = 0;
            }
            if ((tr - 4) >= 0) {
                canDelete = 1;
                tr = tr - 4;
                //       System.out.println("  RIGHTS residui :" + tr);
            } else {
                canDelete = 0;
            }
            if ((tr - 2) >= 0) {
                canModify = 1;
                tr = tr - 2;
                //       System.out.println("  RIGHTS residui :" + tr);
            } else {
                canModify = 0;
            }
            if ((tr - 1) >= 0) {
                canView = 1;
                tr = tr - 1;
                //     System.out.println("  RIGHTS residui :" + tr);
            } else {
                canView = 0;
            }
        }

     
 public smartObjRight createVoidRights() {
        smartObjRight myRight = new smartObjRight(-1);
        return myRight;
    }

    public smartObjRight createNewRights(int rightValue) {
        smartObjRight myRight = new smartObjRight(rightValue);
        return myRight;
    }

}
