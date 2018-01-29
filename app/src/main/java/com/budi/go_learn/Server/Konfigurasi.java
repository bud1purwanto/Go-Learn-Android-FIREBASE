package com.budi.go_learn.Server;

/**
 * Created by root on 11/21/17.
 */

public class Konfigurasi {
    public static String URL_LOGIN_USER             = Server.URL + "/user/login.php";
    public static String URL_REGISTER_USER          = Server.URL + "/user/register.php";
    public static String URL_FOTOPROFIL_USER        = Server.URL + "/user/foto.php";
    public static String URL_EDITPROFIL_USER        = Server.URL + "/user/editprofil.php";
    public static String URL_EDITPROFIL_USERWP      = Server.URL + "/user/editprofilwp.php";
    public static String URL_CHANGEPASSWORD_USER    = Server.URL + "/user/changepassword.php";

    public static String URL_LOGIN_PENGAJAR         = Server.URL + "/pengajar/login.php";
    public static String URL_REGISTER_PENGAJAR      = Server.URL + "/pengajar/register.php";
    public static String URL_FOTOPROFIL_PENGAJAR    = Server.URL + "/pengajar/foto.php";
    public static String URL_EDITPROFIL_PENGAJAR    = Server.URL + "/pengajar/editprofil.php";
    public static String URL_EDITPROFIL_PENGAJARWP  = Server.URL + "/pengajar/editprofilwp.php";

    public static String URL_LIST_PENGAJAR          = Server.URL + "/pengajar/list/list.php";
    public static String URL_CARI_PENGAJAR          = Server.URL + "/pengajar/list/cari.php";

    public static String URL_ORDER_TRANSAKSI        = Server.URL + "/transaksi/order.php";
    public static String URL_WORK                   = Server.URL + "/transaksi/work.php";
    public static String URL_LISTORDER              = Server.URL + "/transaksi/listorder.php";
}
