package com.U1764486.AuctionJavaSpaces.Objects;
import net.jini.core.entry.Entry;
public class U1764486_DeleteLot implements Entry {

    public Integer Lot_id;
    public Boolean delete;
    public U1764486_DeleteLot() { }
    public U1764486_DeleteLot(Integer Lot_id){
        this.Lot_id = Lot_id;
    }
    public U1764486_DeleteLot(Integer Lot_id, Boolean delete)
    {
        this.Lot_id = Lot_id;
        this.delete = delete;
    }
    public Integer getId(){
        return Lot_id;
    }
    public Boolean Deleted(){
        return delete;
    }

}
