package com.prisma.telollevo.utils;

public class ApiResources {

    private static final String URL_SERVER = "http://207.180.212.226/telollevo/api/";

    public static String getLoginUrl(String user, String pass){
        return URL_SERVER+"loginapi?usuario="+user+"&clave="+pass;

    }

    public static String getRegisterUrl(String name, String email, String phone, String pass, String dir){
        return URL_SERVER+"LoginApi?Nombre="+name+"&Correo="+email+"&Telefono="+phone+"&Clave="+pass+"&Direccion="+dir;
    }

    public static String getValidLoginFB(){
        return URL_SERVER+"LoginApi";
    }


    public static String getCategoriesUrl(){
        return URL_SERVER+"CategoriasApi";
    }
   // http://207.180.212.226/telollevo/api/LoginApi?Nombre=flory&Correo=florivr%40ho3tmail.com&Telefono=85858585&Clave=1234&Direccion=2


    public static String getBusinessByCatId(String id){
        return URL_SERVER+"negociosApi?idcategoria="+id;
    }

    public static String getPromoteCat(String id){
        return  URL_SERVER+"promocionesapi?IdCategoria="+id;
    }

    public static String getAllPromotions(){
        return URL_SERVER+"promocionesapi?";
    }

    public static String getFamilyFromBussines(String id){
        return URL_SERVER+"familiaApi?idnegocio="+id;
    }

    public static String getProductsByFamily(String idbus, String idfam){
        return URL_SERVER+"articulosApi?idnegocio="+idbus+"&IdFamilia="+idfam;
    }

    public static String getOrderByUser(String user_id){
        return URL_SERVER+"pedidosApi?Idusuario="+user_id;
    }

    public static String getBussinesById(String bussId){
        return URL_SERVER+"negociounicoApi?idnegocio="+bussId;
    }

    public static String getDeliveryInfo(String delid){
        return URL_SERVER + "repartidorapi?idrepartidor="+delid;
    }

    public static String getOrdersByDeliveryId(String delivId){
        return URL_SERVER+"pedidosrepartidorapi?IdRepartidor="+delivId;
    }

    public static String getUrlServer(){
        return URL_SERVER;
    }
}
