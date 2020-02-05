package com.U1764486.AuctionJavaSpaces;

import com.U1764486.AuctionJavaSpaces.Objects.*;
import com.U1764486.utils.Notifier;
import com.U1764486.utils.SpaceUtils;
import com.U1764486.utils.UserUtils;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import static com.U1764486.utils.UserUtils.getCurrentUser;
public class U1764486_AuctionPanel extends JPanel {

    private final JavaSpace space;
    private final TransactionManager Manager;
    public final ArrayList<U1764486_Lot> Array_Lot;
    public U1764486_Lot lot;
    public U1764486_UpdateLot DeleteLotCheck = null;

    public JTable Table_lot;
    public int TheLotID = -1;

    public Double CurrentBid;
    public String placebid;
    public int button_row;
    public boolean runCheck = false;
    public boolean bought;

    public U1764486_User BidderUserID;
    public U1764486_User Owner;

    JButton Bid_Button = new JButton("Bid");

    public boolean BidAccepted;
    public boolean BidDenied;
    public boolean DeleteLot;

    TableModel tableModel = new DefaultTableModel(new String[0][8], new String[]{
    "Lot ID", "Item Name", "User_Seller ID","Status", "Current Price","Buy Now Price","Desc","Buy button","PlaceBid button","Delete button","Current Bid"});

    class Lot_ButtonRenderer extends JButton implements TableCellRenderer {
        public Lot_ButtonRenderer() {
            Bid_Button = new JButton("Bid");
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object obj,
            boolean Selected, boolean Focus, int Row, int Column) {
            Bid_Button.setText("Bid");
            return this; }
    }
    public U1764486_AuctionPanel(final ArrayList<U1764486_Lot> Array_Lot){
        //UI SETUP
        super(new BorderLayout());
        this.space = SpaceUtils.getSpace();
        this.Manager = SpaceUtils.getManager();

        try {
            //add a lot controller to the space if one does not exist.
            Object Obj = space.read(new U1764486_LotController(), null, 1000);
            if(Obj == null){
                space.write(new U1764486_LotController(0), null, Lease.FOREVER);
            }
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }


        JPanel TxtField = new JPanel(new GridLayout(6, 2));
        TxtField.setBorder(BorderFactory.createEmptyBorder(0, 200, 0, 400));
        //SET LOT & SPACE/MANAGER

        this.Array_Lot = Array_Lot;
        JButton Add_Bttn = new JButton();
        Add_Bttn.setText("Add Auction Item");

   class Bid_ButtonEditor extends DefaultCellEditor {
       public JButton Bid_Button;

       public Bid_ButtonEditor(JCheckBox box) {
        super(box);
        Bid_Button = new JButton("Bid");
        Bid_Button.setOpaque(true);
        Bid_Button.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent el) {
           System.out.println("Button click test");
           button_row = Table_lot.getSelectedRow();

           lot = Array_Lot.get(button_row);

           if (UserUtils.getCurrentUser().equals(lot.getUser()))
           {
               JOptionPane.showMessageDialog(null,"You cannot bid on your own lot");
           } else {
           int BidDialogButton = JOptionPane.YES_NO_OPTION;
           BidDialogButton = JOptionPane.showConfirmDialog(null, "Bid on this lot?", "Bid", BidDialogButton);
           if (BidDialogButton == JOptionPane.YES_OPTION)
           {
               placebid = JOptionPane.showInputDialog(null, "Enter bid ", null);
               if (placebid == null) {
                   JOptionPane.showMessageDialog(null, "Bid not placed, Enter a valid number");
               } else
                   {
                   button_row = Table_lot.getSelectedRow();
                   JOptionPane.showMessageDialog(null, "Bid successful");
                   CurrentBid = Double.parseDouble(placebid);
                   TheLotID = (int) Table_lot.getModel().getValueAt(button_row, 0);
                   lot = Array_Lot.get(button_row);
                   Transaction other_transaction;
                   try {
                       Transaction.Created t = TransactionFactory.create(Manager, 2400);
                       other_transaction = t.transaction;

                       U1764486_LotController tmpl = new U1764486_LotController(lot.getId());
                       U1764486_LotController SpaceReader = (U1764486_LotController) space.read(tmpl, other_transaction, 1500);
                       System.out.println("updating bid object running");
                       //  lot = (U1764486_Lot) space.take(new U1764486_Lot(TheLotID), new_transaction, 1500);
                       // final U1764486_Lot newestLot = (U1764486_Lot) space.read(new U1764486_Lot(TheLotID), null, Constants.1500);
                       //lot.setBothPrices(CurrentBid);
                       BidderUserID = getCurrentUser();
                       runCheck = false;
                       //U1764486_User Bidder;
                       space.write(new U1764486_BidObject(lot.getId(), lot.getItemName(), getCurrentUser(), lot.getUser(), CurrentBid), other_transaction, 2000);
                       System.out.println(" " + lot.getUserId());
                       //space.write(lot, other_transaction, Constants.3000000);
                       other_transaction.commit();
                       space.notify(new U1764486_UpdateLot(), null, new BidManager().Listen(), Lease.FOREVER, null);
                       space.notify(new U1764486_BidObject(), null, new BidChecker().Listen(), Lease.FOREVER, null);
                       space.notify(new U1764486_DeleteLot(), null, new DeleteObj().Listen(), Lease.FOREVER, null);

                       if (FetchModel().getValueAt(button_row, 4) == null) {
                           System.out.println("ROWS DELETED");
                       } else {
                           System.out.println("ROWS AVAILABLE AND SET");
                           FetchModel().setValueAt(CurrentBid, button_row, 4); //(price (column)
                           FetchModel().setValueAt(CurrentBid, button_row, 10); //(current bid column)
                       }


                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                   fireEditingStopped();
                   try {
                       space.notify(new U1764486_UpdateLot(), null, new BidManager().Listen(), Lease.FOREVER, null);
                       space.notify(new U1764486_BidObject(), null, new BidChecker().Listen(), Lease.FOREVER, null);
                       space.notify(new U1764486_DeleteLot(), null, new DeleteObj().Listen(), Lease.FOREVER, null);

                   } catch (Exception e8) {
                       e8.printStackTrace();
                   }
                   }
           }
           if (BidDialogButton == JOptionPane.NO_OPTION)
           {
             remove(BidDialogButton);
           }

           }

           }
        });
       }
            @Override
            public Component getTableCellEditorComponent(JTable table, Object obj, boolean Selected, int Row, int Column) {
           Bid_Button.setText("Bid");
           return Bid_Button;
            }
        }

     class Delete_ButtonEditor extends DefaultCellEditor {
         public JButton Delete_Button;
         public Delete_ButtonEditor(JCheckBox box) {
             super(box);
             Delete_Button = new JButton("Delete");
             Delete_Button.setOpaque(true);
             //Delete_Button.setText("Delete Lot");
     Delete_Button.addActionListener(new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent e) {
                     System.out.println("Delete Button click test");
                     int delete_button_row = Table_lot.getSelectedRow();
                     lot = Array_Lot.get(delete_button_row);
                     if (UserUtils.getCurrentUser().equals(lot.getUser()))
                     {
                         int DeleteDialogButton = JOptionPane.YES_NO_OPTION;
                             DeleteDialogButton = JOptionPane.showConfirmDialog(null, "DELETE this lot?", "WARNING", DeleteDialogButton);

                         if (DeleteDialogButton == JOptionPane.YES_OPTION)
                         {
                             JOptionPane.showMessageDialog(null, "You Deleted this lot");
                             DeleterSet();
                             FetchModel().fireTableDataChanged();
                         }
                             if (DeleteDialogButton == JOptionPane.NO_OPTION) {
                                 remove(DeleteDialogButton);
                             }
                     } else
                         {
                             JOptionPane.showMessageDialog(null, "Access Denied - you dont own this lot");
                         }
                    }
             });
         }
         @Override
         public Component getTableCellEditorComponent(JTable table, Object obj, boolean Selected, int Row, int Column) {
             Delete_Button.setText("Delete Lot");
             return Delete_Button;
         }
     }
        class Buy_ButtonEditor extends DefaultCellEditor
        {
            public JButton Buy_Button;
            public Buy_ButtonEditor(JCheckBox box)
            {
             super(box);
             Buy_Button = new JButton();
             Buy_Button.setOpaque(true);
             Buy_Button.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e)
             {
                System.out.println("Buy Button click test");
                int buy_button_row = Table_lot.getSelectedRow();
                int TheLotID = (int) Table_lot.getModel().getValueAt(buy_button_row, 0);
                System.out.println("lot id = " + TheLotID);
                lot = Array_Lot.get(buy_button_row);
            if (UserUtils.getCurrentUser().equals(lot.getUser())) {
                JOptionPane.showMessageDialog(null,"you can't buy your own lot");
            }
             else {
                int BuyDialogButton = JOptionPane.YES_NO_OPTION;
                BuyDialogButton = JOptionPane.showConfirmDialog(null, "Buy this lot?", "WARNING", BuyDialogButton);

                if (BuyDialogButton == JOptionPane.YES_OPTION) {

                    // buy lot and delete the lot
                    JOptionPane.showMessageDialog(null,"You Bought this lot");
                    bought = true;
                    DeleterSet();
                    //FetchModel().removeRow(buy_button_row);
                    FetchModel().fireTableDataChanged();

                }if (BuyDialogButton == JOptionPane.NO_OPTION)
                {
                    JOptionPane.showMessageDialog(null,"Cancelled");
                    remove(BuyDialogButton);
                }
            }
             fireEditingStopped(); }});
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object obj, boolean Selected, int Row, int Column) {
                Buy_Button.setText("Buy Lot");
                return Buy_Button;
            }}


            //buy now price and start prices.


      //  TxtField.add(new JLabel("Logged In as " + UserUtils.getCurrentUser()));
  final JTextField _Price = new JTextField("", 1); TxtField.add(new JLabel("Start Price £: "));TxtField.add(_Price);
  final JTextField _BuyNow_Price = new JTextField("", 1); TxtField.add(new JLabel("Buy Now Price £: "));TxtField.add(_BuyNow_Price);

 //name and description
   final JTextField _Name = new JTextField("", 1); TxtField.add(new JLabel("Name of this item: "));TxtField.add(_Name);
   final JTextField _Description = new JTextField("", 1); TxtField.add(new JLabel("Description: "));TxtField.add(_Description);
   final JLabel MssgText = new JLabel();TxtField.add(new JLabel("Lot Status: "));

 TxtField.add(MssgText);

        add(TxtField,BorderLayout.WEST);
        
        Table_lot = new JTable(tableModel);
        JScrollPane list = new JScrollPane(Table_lot, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(list, BorderLayout.NORTH);

        Add_Bttn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //REST/
                FetchModel().fireTableDataChanged();
                MssgText.setText("");


                int AskDialogButton = JOptionPane.YES_NO_OPTION;
                AskDialogButton = JOptionPane.showConfirmDialog(null, "Add this lot to AuctionJavaSpaces?", "WARNING", AskDialogButton);

                if (AskDialogButton == JOptionPane.YES_OPTION) {
                    String Name_Item = _Name.getText();
                    String itemDescription = _Description.getText();
                    //int price_changed = Integer.parseInt(_Price.getText());
                    Double price_changed = Double.parseDouble(_Price.getText());
                    Double buynow_changed = Double.parseDouble(_BuyNow_Price.getText());
                    Table_lot.getColumn("Delete button").setCellRenderer(new Lot_ButtonRenderer());
                    Table_lot.getColumn("Delete button").setCellEditor(new Delete_ButtonEditor(new JCheckBox()));
                    Table_lot.getColumn("Buy button").setCellEditor(new Buy_ButtonEditor(new JCheckBox()));
                    Table_lot.getColumn("Buy button").setCellRenderer(new Lot_ButtonRenderer());
                    Table_lot.getColumn("PlaceBid button").setCellRenderer(new Lot_ButtonRenderer());
                    Table_lot.getColumn("PlaceBid button").setCellEditor(new Bid_ButtonEditor(new JCheckBox()));
                    Transaction _transaction = null;
                    try {
                        Transaction.Created t = TransactionFactory.create(Manager, 2400);
                        //CHECK IF q = NULL to stop all services.
                        if (t == null)
                        {
                            JOptionPane.showMessageDialog(null, "Program failed" );
                            System.exit(1);
                        }
                        _transaction = t.transaction;
                  JOptionPane.showMessageDialog(null,"Starting price is set to: £"+ new Double(price_changed));
                // JOptionPane.showMessageDialog(null,new Double(buynow_changed));




                        U1764486_LotController FetchLot = (U1764486_LotController) space.take(new U1764486_LotController(), _transaction, 1500);
                        final int lotID = FetchLot.NewItem();

                        U1764486_Lot _Addlot = new U1764486_Lot(lotID, getCurrentUser(), Name_Item, price_changed, buynow_changed, itemDescription, 0.0);
                        space.write(_Addlot, _transaction, 1500);
                        space.write(FetchLot, _transaction, Lease.FOREVER);
                        _transaction.commit();
                        //SUCCESS MSG TO USER
                        MssgText.setText("Lot Successful");
                        Array_Lot.add(_Addlot);
                        //ADD TO TABLE
                        FetchModel().addRow(_Addlot.SetArray());
                       // FetchModel().setValueAt("No bid Yet", lotID, 10);

                        // - EMPTY FIELDS
                        _BuyNow_Price.setText("");
                        _Price.setText("");
                        _Name.setText("");
                        _Description.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (AskDialogButton == JOptionPane.NO_OPTION) {
                    remove(AskDialogButton);
                }



                try {
                    //NOTIFY (UPDATE SPACE)
                    space.notify(new U1764486_LotController(), null, new New_LotChecker().Listen(), Lease.FOREVER, null);
                    // space.notify(new U1764486_UpdateLot(), null, new BidManager().Listen(), Lease.FOREVER, null);
                    //space.notify(new U1764486_DeleteLot(), null, new DeleteNotify().Listen(), Lease.FOREVER, null);
                    space.notify(new U1764486_UpdateLot(), null, new BidManager().Listen(), Lease.FOREVER, null);
                    space.notify(new U1764486_BidObject(), null, new BidChecker().Listen(), Lease.FOREVER, null);
                    space.notify(new U1764486_DeleteLot(), null, new DeleteObj().Listen(), Lease.FOREVER, null);
                    space.notify(null, null, new BidAccepted().Listen(), Lease.FOREVER, null);
                    space.notify(null, null, new BidDenied().Listen(), Lease.FOREVER, null);
                    space.notify(new U1764486_LotController(), null, new New_LotChecker().Listen(), Lease.FOREVER, null);
                    System.out.println("space is notified");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        JPanel Auction_Window = new JPanel(new FlowLayout());
        Auction_Window.add(Add_Bttn); add(Auction_Window, BorderLayout.CENTER);

        try {
            //NOTIFY (UPDATE SPACE)
            space.notify(new U1764486_LotController(), null, new New_LotChecker().Listen(), Lease.FOREVER, null);
            // space.notify(new U1764486_UpdateLot(), null, new BidManager().Listen(), Lease.FOREVER, null);
            //space.notify(new U1764486_DeleteLot(), null, new DeleteNotify().Listen(), Lease.FOREVER, null);
            space.notify(new U1764486_UpdateLot(), null, new BidManager().Listen(), Lease.FOREVER, null);
            space.notify(new U1764486_BidObject(), null, new BidChecker().Listen(), Lease.FOREVER, null);
            space.notify(new U1764486_DeleteLot(), null, new DeleteObj().Listen(), Lease.FOREVER, null);
            space.notify(null, null, new BidAccepted().Listen(), Lease.FOREVER, null);
            space.notify(null, null, new BidDenied().Listen(), Lease.FOREVER, null);
            space.notify(new U1764486_LotController(), null, new New_LotChecker().Listen(), Lease.FOREVER, null);
            System.out.println("space is notified");

        } catch (Exception e) {
            e.printStackTrace();
        }

       // System.exit(0);
        //----------------------END OF AUCTIONROOM()
    }

    public DefaultTableModel FetchModel(){
        return ((DefaultTableModel) Table_lot.getModel());
    }

    private class New_LotChecker extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel fetchmodel = FetchModel();
            FetchModel().fireTableDataChanged();
            try {
                // Grab the latest version of the U1764486_LotController and the latest lot from the Space
                U1764486_LotController FetchLot = (U1764486_LotController) space.read(new U1764486_LotController(), null, 1000);
                U1764486_Lot newerLot = (U1764486_Lot) space.read(new U1764486_Lot(FetchLot.fetchItemNO()), null, 1000);

                // Convert the lot to an Object[][]
                Object[] toObj = newerLot.SetArray();
            System.out.println("checking for new lot");
                // find out if the lot already exist
                int TableIndex = -1;
                for(int q = 0, p = Array_Lot.size(); q < p; q++){

                    if
                    (Array_Lot.get(q).getId().intValue() == newerLot.getId().intValue())
                    {
                        TableIndex = q;
                        break;
                    }
                }
                //add if non exist
                if(TableIndex == -1) {
                    Array_Lot.add(newerLot);
                    fetchmodel.addRow(toObj);
                } else {
                    // Update lot
                    Array_Lot.set(TableIndex, newerLot);
                    fetchmodel.setValueAt(toObj[4], TableIndex, 4);
                    fetchmodel.setValueAt("No bid Yet", TableIndex, 10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class BidManager extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            FetchModel().fireTableDataChanged();
            DefaultTableModel fetchmodel = FetchModel();
            try {
                // Read the latest U1764486_UpdateLot object from the Space (there should only be one)
             U1764486_UpdateLot lotChange = (U1764486_UpdateLot) space.read(new U1764486_UpdateLot(), null, 1500);
             //check if lotChange is null
                if (lotChange == null)
                {
                    System.out.println("lotChange null = FINISHED");
                }
                else{
                    //set BidderUserID to getUser from the lotChange.
                    BidderUserID = lotChange.getUser();
                    //SET THE DeleteLotCheck to be same as lotChange.
                    DeleteLotCheck = lotChange;
                    }
                // Find the existing index of the lot with a matching Lot_id
                int TableIndex = -1;
                for(int q = 0, p = Array_Lot.size(); q < p; q++){

                    //CHECKS ALL LOTS ONE BY ONE TO FIND A MATCH.
                    if(Array_Lot.get(q).getId().intValue() == lotChange.getId()){
                      //  System.out.println("(client) lotchange.getid (BidManager) = " + lotChange.getId());

                        //CURRENTINDEX = THE LOT WHICH MATCHED THE SEARCH.
                        TableIndex = q;
                        break;
                    }
                }

                if(TableIndex == -1){
                    System.out.println("lot updater FAILED (TableIndex = -1 -RETURN)");
                    return;
                }
                // GET LOT THAT MATCHED and SET LOT1 TO EQUAL THE MATCHED LOT. (TABLE LOT index NOT ACTUAL LOTid)
                U1764486_Lot lot1 = Array_Lot.get(TableIndex);
                //Lot_to_Delete = lot1;
                // Apply the BID to the lot
                lot1.setBothPrices(lotChange.getPrice());
                // Convert to an Object[][]
                Object[] toObj = lot1.SetArray();
            // Replace the lot in the local list and table with the changed lot
                Array_Lot.set(TableIndex, lot1);

                //change the values in the table.
                fetchmodel.setValueAt(toObj[4], TableIndex, 4);
                fetchmodel.setValueAt(toObj[4], TableIndex, 10);
                //update table onscreen.
                FetchModel().fireTableDataChanged();

                System.out.println("BOTH BIDS UPDATED");
            } catch (Exception ei9) {
                ei9.printStackTrace();
            }

        }
    }

    private class BidAccepted extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            FetchModel().fireTableDataChanged();
            if (BidAccepted) {

                if (UserUtils.getCurrentUser().equals(BidderUserID)) {

                    // space.notify(new U1764486_DeleteLot(), null, new DeleteNotify().Listen(), Lease.FOREVER, null);
                    JOptionPane.showMessageDialog(null, "Bid was Accepted!");
                    //reset both variables
                    BidAccepted = false;
                    BidderUserID = null;
                    System.out.println("ALERT ALL CLIENTS AGAIN");
                    DeleterSet();
                    // DeleteLot = true;
                } else {
                    //show this to all other users
                    JOptionPane.showMessageDialog(null, "An item has been sold!" + " Bought by User: " + BidderUserID);
                    //reset both variables
                    BidderUserID = null;
                    BidAccepted = false;
                }
            }
        }
    }


    private class BidDenied extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {

            if (BidDenied)
            {
                if (UserUtils.getCurrentUser().equals(BidderUserID))
                {
                    System.out.println("bid deny");
                    JOptionPane.showMessageDialog(null, "Your Bid was Denied! - Bid Higher");
                    //reset both variables
                    BidderUserID = null;
                    BidDenied = false;
                    Bidding();
                }
            }
        }
    }

    private class DeleteObj extends Notifier
    {
        @Override
        public void notify(RemoteEvent ev) {

                DefaultTableModel fetchmodel = FetchModel();
                try {
                    U1764486_DeleteLot Delete_Obj = (U1764486_DeleteLot) space.read(new U1764486_DeleteLot(), null, 1500);
                    int TableIndex = -1;

                        for (int q = 0, p = Array_Lot.size(); q < p; q++) {
                            if (Array_Lot.get(q).getId().intValue() == Delete_Obj.getId()) {
                                //  System.out.println("(client) lotchange.getid (BidManager) = " + lotChange.getId());
                                TableIndex = q;
                                break;
                            }
                        }
                        if (TableIndex == -1) {

                            return;
                        }

                        U1764486_Lot lot1 = Array_Lot.get(TableIndex);
                        Array_Lot.set(TableIndex, lot1);
                        Array_Lot.remove(TableIndex);
                        fetchmodel.removeRow(TableIndex);
                        space.takeIfExists(new U1764486_Lot(Delete_Obj.getId()), null, 1000);
                        FetchModel().fireTableDataChanged();
                        DeleteLot = false;
                        System.out.println("delete lot = FINISHED");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                DeleteLot=false;

        }
    }

    private class BidChecker extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel fetchmodel = FetchModel();
                try {
                    U1764486_BidObject BidObj = (U1764486_BidObject) space.read(new U1764486_BidObject(), null, 1500);
                    //runCheck is there to make sure code only runs once.

                    if (UserUtils.getCurrentUser().equals(BidObj.getOwner()) && runCheck == false) {
                        // new JButton = JOptionPane.showConfirmDialog(null, "New bid for your item: " + lot1.getItemName() + " BID AMOUNT: " + toObj[4]+ "Accept bid and Sell item?", "New Bid",
                        // JOptionPane.YES_NO_OPTION);
                        if (JOptionPane.showConfirmDialog(null, "New bid for your item: " + BidObj.getItemName() + " BID AMOUNT: " + BidObj.getBid()
                                + "Accept bid and Sell item?", "New Bid", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                        {
                            BidAccepted = true;
                            runCheck = true;
                            JOptionPane.showMessageDialog(null, "An ITEM IS SOLD!");
                            BidderUserID = BidObj.getBidder();
                            Owner = BidObj.getOwner();
                            DeleterSet();
                            //delete this lot.
                        }
                        else
                        {
                            System.out.println("bid denied");
                            BidDenied = true;
                            runCheck = true;
                            JOptionPane.showMessageDialog(null, "You Denied This Bid! - All Users Notified");
                            BidderUserID = BidObj.getBidder();
                            Owner = BidObj.getOwner();
                            //save bid to both tables and notify
                            int TableIndex = -1;
                            for(int q = 0, p = Array_Lot.size(); q < p; q++){

                                //CHECKS ALL LOTS ONE BY ONE TO FIND A MATCH.
                                if(Array_Lot.get(q).getId().intValue() == BidObj.getId()){
                                    TableIndex = q;
                                    break;
                                }
                            }
                            if(TableIndex == -1){
                                System.out.println("lot updater FAILED (TableIndex = -1 -RETURN)");
                                return;
                            }
                            U1764486_Lot lot1 = Array_Lot.get(TableIndex);
                            Array_Lot.set(TableIndex, lot1);
                            //change the values in the table.
                            CurrentBid = BidObj.getBid();
                            fetchmodel.setValueAt(CurrentBid, TableIndex, 4);
                            fetchmodel.setValueAt(CurrentBid, TableIndex, 10);
                            //update table onscreen.
                            FetchModel().fireTableDataChanged();

                        }
                        //runCheck - code only runs once regardless of notify.
                        runCheck = true;
                    }
                    //to only do it once
                    runCheck = true;

                } catch (Exception ei9) {
                    ei9.printStackTrace();
                }
        }
    }

    public void DeleterSet()
    {

        Transaction other_transaction;
        try {
            Transaction.Created t = TransactionFactory.create(Manager, 2400);
            other_transaction = t.transaction;
            lot = Array_Lot.get(button_row);

            space.write(new U1764486_DeleteLot(lot.getId(),true), other_transaction, 2000);
            //space.write(lot, other_transaction, Constants.3000000);
            other_transaction.commit();

            //remove from array and table for local.
            Array_Lot.remove(button_row);
            FetchModel().removeRow(button_row);

            if (bought)
            {
                JOptionPane.showMessageDialog(null, "A lot was bought called : " + lot.getItemName());
            }


            JOptionPane.showMessageDialog(null, "A lot was deleted called : " + lot.getItemName());
           // space.notify(new U1764486_DeleteLot(), null, new DeleteObj().Listen(), Lease.FOREVER, null);
        }catch (Exception ei9) {
            ei9.printStackTrace();
        }
    }


    public void Bidding() {

        Transaction other_transaction;
        try {
            Transaction.Created t = TransactionFactory.create(Manager, 2400);
            other_transaction = t.transaction;

            System.out.println("BIDDING running");
            lot = Array_Lot.get(button_row);
             //lot = (U1764486_Lot) space.take(new U1764486_Lot(TheLotID), new_transaction, 1500);
            // final U1764486_Lot newestLot = (U1764486_Lot) space.read(new U1764486_Lot(TheLotID), null, Constants.1500);

            lot.setBothPrices(CurrentBid);
            BidderUserID = getCurrentUser();

            //U1764486_User Bidder;
            space.write(new U1764486_UpdateLot(lot.getId(), CurrentBid, CurrentBid, getCurrentUser(), DeleteLot), other_transaction, 2000);
            System.out.println("the lot id (placebid) is " + lot.getUserId());
            space.write(lot, other_transaction, 3000000);
            other_transaction.commit();

            FetchModel().setValueAt(CurrentBid, button_row, 4); //(price (column)
            FetchModel().setValueAt(CurrentBid, button_row, 10); //(current bid column)
            //reset the runCheck
            runCheck = false;
            space.notify(new U1764486_UpdateLot(), null, new BidManager().Listen(), Lease.FOREVER, null);
            System.out.println("BIDDING ended");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
