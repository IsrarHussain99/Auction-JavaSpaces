package com.U1764486.AuctionJavaSpaces.Objects;

import java.util.Objects;

import com.U1764486.AuctionJavaSpaces.U1764486_AuctionPanel;
import net.jini.core.entry.Entry;

public class U1764486_Lot implements Entry {
    public Integer Lot_id;
    public Integer CurrentLotID;
    public Double Cost;
    public Double CurrentBid;
    public Double BuyNow;
    public String Name_of_Seller;
    public String Lot_Info;
    public U1764486_User User_Seller;
    U1764486_AuctionPanel auctionPanel;
    public U1764486_Lot(){ }
    public U1764486_Lot(Integer Lot_id){
        this.Lot_id = Lot_id;
    }
    public U1764486_Lot
   (Integer Lot_id, U1764486_User User_Seller, String Name_of_Seller, Double Cost, Double BuyNow, String Lot_Info, Double CurrentBid){
        // (lotid, userseller, nameofseller,cost,buynow,lotinfo,lotfinished_Check,)

        this.Lot_id = Lot_id;
        this.User_Seller = User_Seller;
        this.Name_of_Seller = Name_of_Seller;
        this.Cost = Cost;
        this.Lot_Info = Lot_Info;
        this.BuyNow = BuyNow;
        this.CurrentBid = CurrentBid;
    }

    public Integer getId(){
        return Lot_id;
    }
    public Integer getCurrentLotId(){
        return Lot_id;
    }
    public U1764486_User getUser(){
        return User_Seller;
    }
    public String getUserId(){
        return User_Seller.getId();
    }
    public String getItemName(){
        return Name_of_Seller;
    }
    public Double getCurrentPrice(){
        return Cost;
    }
    public Double getBuyNowPrice(){

        return BuyNow;
    }
    public U1764486_Lot setId(Integer Lot_id){
        this.Lot_id = Lot_id;
        return this;
    }
    public U1764486_Lot setUser(U1764486_User User_Seller){
        this.User_Seller = User_Seller;
        return this;
    }
    public U1764486_Lot setBothPrices(Double Cost){

        this.Cost = Cost;
        //this.BuyNow = BuyNow;
        return this;
    }
    public Object[] SetArray(){ return new Object[]{Lot_id, Name_of_Seller, User_Seller == null ? null : User_Seller.getId(), Cost,BuyNow,Lot_Info}; }

    @Override
    public boolean equals(Object Obj){ if (this == Obj) return true;if (Obj == null || getClass() != Obj.getClass())
        return false;U1764486_Lot that = (U1764486_Lot) Obj;
        return
                Objects.equals(this.Lot_id, that.Lot_id) &&
                Objects.equals(this.User_Seller, that.User_Seller) &&
                Objects.equals(this.Name_of_Seller, that.Name_of_Seller) &&
                Objects.equals(this.Cost, that.Cost) &&
                Objects.equals(this.Lot_Info, that.Lot_Info) &&
                        Objects.equals(this.BuyNow, that.BuyNow);


    }
}
