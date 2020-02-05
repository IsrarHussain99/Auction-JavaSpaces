package com.U1764486.utils;

import com.U1764486.AuctionJavaSpaces.Objects.U1764486_User;

public final class UserUtils {

    private static U1764486_User User_Seller;
    private UserUtils(){
        throw new UnsupportedOperationException();
    }

    public static U1764486_User getCurrentUser(){
        return User_Seller;
    }

    public static U1764486_User setCurrentUser(String username){
        User_Seller = new U1764486_User(username);
        System.out.println("Registered new user: " + username);
        return User_Seller;
    }

}
