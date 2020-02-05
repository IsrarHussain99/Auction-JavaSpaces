package com.U1764486.AuctionJavaSpaces.Objects;

import java.io.Serializable;
import java.util.Objects;

public class U1764486_User implements Serializable {
    public final String Lot_id;
    public U1764486_User(String Lot_id){
        this.Lot_id = Lot_id;
    }
    public String getId(){
        return Lot_id;
    }
    @Override
    public boolean equals(Object Obj){
        if (this == Obj)
            return true;
        if (Obj == null || getClass() != Obj.getClass())
            return false;
        U1764486_User that = (U1764486_User) Obj;
        return Objects.equals(this.Lot_id, that.Lot_id);
    }
}
