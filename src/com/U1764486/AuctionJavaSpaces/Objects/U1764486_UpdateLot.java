package com.U1764486.AuctionJavaSpaces.Objects;

import com.U1764486.AuctionJavaSpaces.U1764486_AuctionPanel;
import net.jini.core.entry.Entry;
public class U1764486_UpdateLot implements Entry {
    public Integer Lot_id;
    public Double Cost;
    public Double CurrentBid;
    public Double CurrentBid1;
    boolean DeleteThisLot;
    public U1764486_User Bidder;
    U1764486_AuctionPanel auctionPanel;
    public U1764486_UpdateLot(){ }
    public U1764486_UpdateLot(Integer Lot_id){
        this.Lot_id = Lot_id;
    }
    public U1764486_UpdateLot(Integer Lot_id, Double Cost, Double CurrentBid, U1764486_User Bidder, boolean DeleteThisLot){
        this.Lot_id = Lot_id;
        this.CurrentBid = CurrentBid;
        this.Cost = Cost;
        this.Bidder = Bidder;
        this.DeleteThisLot = DeleteThisLot;
      //  this.Bidder = Bidder;
        System.out.println(CurrentBid + " NEXT" + CurrentBid); }
    public Integer getId(){
        return Lot_id;
    }
    public U1764486_User getUser(){
        return Bidder;
    }
    public Double getPrice(){
        return Cost;
    }
    public boolean getDeleteCheck(){
        return DeleteThisLot;
    }


}