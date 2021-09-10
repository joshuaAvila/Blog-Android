package com.example.proyecto_app;

public class Constantes {
    public static final String url =  "http://192.168.1.30:8001/";
    public static final String home = url+"api";
    public static final String login =  home+"/login";
    public static final String logout =  home+"/logout";
    public static  final String register = home+"/register";
    public static  final String save_userInfo = home+"/save_user_info";
    public static  final String posts = home+"/posts";
    public static  final String create_post = posts+"/create";
    public static final String update_post = posts+"/update";
    public static final String delete_post = posts+"/delete";
    public static final String  like_post = posts+"/like";

    public static  final String comentarios = posts+"/comments";
    public static  final String create_comentarios = home+"/comments/create";
    public static  final String delete_comentarios = home+"/comments/delete";

    public static  final String my_posts = posts+"/mis_publicaciones";


}
