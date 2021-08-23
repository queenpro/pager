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

import java.util.List;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 *
 * @author FFS INFORMATICA <info at ffs.it>
 */
public class smartCard {

    public String dati;
    public String dati_personali;
    public String c_carta;
    public String id_carta;
    public String inst_filev;
    public String servizi_installati;
    public String gdo;
    public String key_pub;
    public String codice_emettitore;
    public String data_rilascio_tessera;
    public String data_scadenza_tessera;
    public String cognome;
    public String nome;
    public String data_nascita;
    public String sesso;
    public String statura;
    public String cf;
    public String Cittadinanza;
    public String Comune_nascita;
    public String Stato_nascita;
    public String atto_nascita;
    public String Comune_residenza;
    public String Indirizzo_residenza;
    public String annotazione_espatrio;

    public void readCard() {
        byte[] SELECT_FILE_APDU = {(byte) 0x00, (byte) 0xA4, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x11, (byte) 0x00, (byte) 0x11, (byte) 0x02, (byte) 0x00};
        byte[] READ_BINARY_APDU = {(byte) 0x00, (byte) 0xB0, (byte) 0x00, (byte) 0x00, (byte) 0xff};
        String stringa_dati_personali = "";
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals;
            terminals = factory.terminals().list();
            System.out.println("Reader: " + terminals);
            CardTerminal terminal = terminals.get(0);
            Card card = terminal.connect("*");
            System.out.println("Card: " + card);
            CardChannel channel = card.getBasicChannel();
            try {
                ATR atr = card.getATR();
                byte[] ATR = atr.getBytes();
                System.out.println("ATR della carta: " + bytesToHex(ATR));
//Per determinare quale wrapper PCKS11 deve essere utilizzato e' importante ottenere l'ATR del dispositivo.

                //GET SELECT_FILE_APDU
//                byte[] dati_personali = {(byte) 0x00, (byte) 0xA4, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x11, (byte) 0x00, (byte) 0x11, (byte) 0x02, (byte) 0x00};
                byte[] dati_personali = {(byte) 0x00, (byte) 0xA4, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x11, (byte) 0x00, (byte) 0x11, (byte) 0x02, (byte) 0x00};

                byte[] c_carta = {(byte) 0x00, (byte) 0xA4, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x11, (byte) 0x00, (byte) 0x11, (byte) 0x01, (byte) 0x00};
                byte[] id_carta = {(byte) 0x00, (byte) 0xA4, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x10, (byte) 0x00, (byte) 0x10, (byte) 0x03, (byte) 0x00};
                byte[] inst_file = {(byte) 0x00, (byte) 0xA4, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x12, (byte) 0x00, (byte) 0x41, (byte) 0x42, (byte) 0x00};
                byte[] servizi_installati = {(byte) 0x00, (byte) 0xA4, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x12, (byte) 0x00, (byte) 0x12, (byte) 0x03, (byte) 0x00};
                byte[] gdo = {(byte) 0x00, (byte) 0xA4, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x3F, (byte) 0x00, (byte) 0x2F, (byte) 0x02, (byte) 0x00};
                byte[] key_pub = {(byte) 0x00, (byte) 0xA4, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x3F, (byte) 0x00, (byte) 0x3F, (byte) 0x01, (byte) 0x00};
//                richiedi(channel, READ_BINARY_APDU, SELECT_FILE_APDU, "0.Dati  : ");
                stringa_dati_personali = richiedi(channel, READ_BINARY_APDU, dati_personali, "1.Dati personali: ");

//                richiedi(channel, READ_BINARY_APDU, c_carta, "2.Carta: ");
//                richiedi(channel, READ_BINARY_APDU, id_carta, "3.ID Carta: ");
//                richiedi(channel, READ_BINARY_APDU, inst_file, "4.Inst File: ");
//
//                richiedi(channel, READ_BINARY_APDU, servizi_installati, "5.Servizi installati: ");
//                richiedi(channel, READ_BINARY_APDU, gdo, "6.GDO: ");
//                richiedi(channel, READ_BINARY_APDU, key_pub, "7.KEY PUB: ");
                // Disconnect the card
            } catch (Exception e) {
                System.out.println("Ouch: " + e.toString());
            }
            card.disconnect(false);
            System.out.println("DISCONEESSO ");
        } catch (CardException ex) {

            System.out.println("ERRORE in Accesso al Reader . ");
        }

        int dimensione = Integer.parseInt(stringa_dati_personali.substring(0, 6), 16);
        System.out.println("dimensione: " + dimensione);
        int prox_field_size = Integer.parseInt(stringa_dati_personali.substring(6, 8), 16);
        int da = 8;
        int a = da + prox_field_size;
        if (prox_field_size > 0) {
            codice_emettitore = stringa_dati_personali.substring(da, a);
            System.out.println("Codice emettitore: " + codice_emettitore);
        }

        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            data_rilascio_tessera = stringa_dati_personali.substring(da, a);
            System.out.println("data_rilascio_tessera: " + data_rilascio_tessera);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            data_scadenza_tessera = stringa_dati_personali.substring(da, a);
            System.out.println("data_scadenza_tessera: " + data_scadenza_tessera);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            cognome = stringa_dati_personali.substring(da, a);
            System.out.println("cognome: " + cognome);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            nome = stringa_dati_personali.substring(da, a);
            System.out.println("nome: " + nome);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            data_nascita = stringa_dati_personali.substring(da, a);
            System.out.println("data_nascita: " + data_nascita);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            sesso = stringa_dati_personali.substring(da, a);
            System.out.println("sesso: " + sesso);
        }
        da = a;
        a += 2;

        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            statura = stringa_dati_personali.substring(da, a);
            System.out.println("statura: " + statura);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            cf = stringa_dati_personali.substring(da, a);
            System.out.println("cf: " + cf);
        }

        /*
        String Cittadinanza;
String Comune_nascita;
String Stato_nascita;
String atto_nascita;
String Comune_residenza ;
String Indirizzo_residenza;
String   annotazione_espatrio;
         */
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            Cittadinanza = stringa_dati_personali.substring(da, a);
            System.out.println("Cittadinanza: " + Cittadinanza);
        }else{
            System.out.println("Cittadinanza: prox_field_size=0 ..." + prox_field_size);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            Comune_nascita = stringa_dati_personali.substring(da, a);
            System.out.println("Comune_nascita: " + Comune_nascita);
        }else{
            System.out.println("Comune_nascita: prox_field_size=0 ..." + prox_field_size);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            Stato_nascita = stringa_dati_personali.substring(da, a);
            System.out.println("Stato_nascita: " + Stato_nascita);
        }else{
            System.out.println("Stato_nascita: prox_field_size=0 ..." + prox_field_size);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            atto_nascita = stringa_dati_personali.substring(da, a);
            System.out.println("atto_nascita: " + atto_nascita);
        }else{
            System.out.println("atto_nascita: prox_field_size=0 ..." + prox_field_size);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            Comune_residenza = stringa_dati_personali.substring(da, a);
            System.out.println("Comune_residenza: " + Comune_residenza);
        }else{
            System.out.println("Comune_residenza: prox_field_size=0 ..." + prox_field_size);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            Indirizzo_residenza = stringa_dati_personali.substring(da, a);
            System.out.println("Indirizzo_residenza: " + Indirizzo_residenza);
        }else{
            System.out.println("Indirizzo_residenza: prox_field_size=0 ..." + prox_field_size);
        }
        da = a;
        a += 2;
        prox_field_size = Integer.parseInt(stringa_dati_personali.substring(da, a), 16);
        da = a;
        a += prox_field_size;
        if (prox_field_size > 0) {
            annotazione_espatrio = stringa_dati_personali.substring(da, a);
            System.out.println("annotazione_espatrio: " + annotazione_espatrio);
        }else{
            System.out.println("annotazione_espatrio: prox_field_size=0 ..." + prox_field_size);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }

        return sb.toString();
    }

    public String richiedi(CardChannel channel, byte[] read, byte[] select, String titolo) throws CardException {

        // Send Select Applet command
        ResponseAPDU answer = channel.transmit(new CommandAPDU(select));
//        System.out.println("\n\n"+titolo + " PURE BYTES: " + answer.getBytes());
//        String hex = DatatypeConverter.printHexBinary(answer.getBytes());
//        System.out.println(titolo + " HEX: " + hex);
        ResponseAPDU r = channel.transmit(new CommandAPDU(read));

        System.out.println("HEX> " + bytesToHex(r.getData()));
////////        System.out.println("2.Response B: " + new String(r.getBytes()));
////////        hex = DatatypeConverter.printHexBinary(r.getBytes());
////////        System.out.println("2.Response H:" + hex);

//ResponseAPDU r = channel.transmit(new CommandAPDU(0x00, 0x84, 0x00, 0x00, 0x08));
        // Send test command
////        answer = channel.transmit(new CommandAPDU(read));
        byte rr[] = r.getData();
        String test = "";
        for (int i = 0; i < rr.length; i++) {
            test += (char) rr[i];

        }
        System.out.println(titolo + ": READ >>> " + test);
        return test;
    }
}
