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

import REVOmail.EmailSender;
import REVOmail.EmailSessionBean;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class mailSender {
    

    public int sendMail(String dest, String oggetto, String body) {
        
//        System.out.println("SONO IN mailSender--> SENDMAIL");
        EmailSender mailer = new EmailSender();
        
        mailer.SendEmail(dest, oggetto, body);
        return 1;
    }
}
