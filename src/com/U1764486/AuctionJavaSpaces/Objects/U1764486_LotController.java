package com.U1764486.AuctionJavaSpaces.Objects;
import net.jini.core.entry.Entry;

public class U1764486_LotController implements Entry {
    public Integer ItemNumber;
    public U1764486_LotController() {
        super();
    }
    public U1764486_LotController(int ItemNumber){
        this.ItemNumber = ItemNumber;
    }
    public Integer NewItem(){
        return ++ItemNumber;
    }
    public Integer fetchItemNO(){
        return ItemNumber;
    }
}