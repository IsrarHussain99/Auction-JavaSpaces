package com.U1764486.AuctionJavaSpaces.Objects;

import net.jini.core.entry.Entry;

public class U1764486_BidObject implements Entry {

    public Integer Lot_id;
    public Double Bid;
    public String ItemName;
    public U1764486_User Bidder;
    public U1764486_User Owner;

    public U1764486_BidObject() { }

    public U1764486_BidObject(Integer Lot_id){
        this.Lot_id = Lot_id;
    }
    public U1764486_BidObject(Integer Lot_id, String ItemName, U1764486_User Bidder, U1764486_User Owner, Double Bid)
    {
        this.Lot_id = Lot_id;
        this.ItemName = ItemName;
        this.Bidder = Bidder;
        this.Bid = Bid;
        this.Owner = Owner;
    }

    public Integer getId(){
        return Lot_id;
    }

    public Double getBid(){
        return Bid;
    }

    public String getItemName(){
        return ItemName;
    }

    public U1764486_User getOwner(){
        return Owner;
    }

    public U1764486_User getBidder(){
        return Bidder;
    }


}
