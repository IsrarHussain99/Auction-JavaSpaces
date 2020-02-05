package com.U1764486;
import com.U1764486.AuctionJavaSpaces.Objects.U1764486_LotController;
import java.util.ArrayList;
import com.U1764486.AuctionJavaSpaces.Objects.U1764486_Lot;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.U1764486.AuctionJavaSpaces.U1764486_AuctionPanel;
import com.U1764486.utils.UserUtils;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;
import com.U1764486.utils.SpaceUtils;


public class Room extends JFrame {

    private final ArrayList<U1764486_Lot> lots = new ArrayList<U1764486_Lot>();
    public JavaSpace space;
    public String userId;


    public U1764486_AuctionPanel ap;

    public static void main(String[] args) {

         String userId = JOptionPane.showInputDialog(null, " Enter your Username: ", null);
        //CHECK FOR USER ENTRY


        //asssign user if not entered
        if(userId == null || userId.length() == 0){
            //own
            int UserCount = userId.length() + 1;
            userId = "GuestUser" + UserCount;
        }
        //SETS USER

        UserUtils.setCurrentUser(userId);
        new Room();
    }


    public Room() {
       // ap.userID = userId;

        space = SpaceUtils.getSpace();
        if (space == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }

    try { // CHECK THIS
            // make sure an U1764486_LotController is setup in the Space
            Object Obj = space.read(new U1764486_LotController(), null, 1000);
            if(Obj == null){
                space.write(new U1764486_LotController(0), null, Lease.FOREVER);
            }
            } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        Container cont = getContentPane();
        cont.setLayout(new BorderLayout());
        JPanel WindowPanel = new JPanel(new CardLayout());
        setTitle("Room");
        final U1764486_AuctionPanel AuctionWindow = new U1764486_AuctionPanel(lots);
        WindowPanel.add(AuctionWindow);
        cont.add(WindowPanel);
        setVisible(true);
        pack();

        //new mainClass();

        new Thread(() -> {
            DefaultTableModel model = AuctionWindow.FetchModel();
            try {
                //check if there are other lots before.
                U1764486_LotController lot_ = (U1764486_LotController) space.read(new U1764486_LotController(), null, 4500);

                System.out.println("MAINCLASS RUNNING");
                // Loop for all item ids
                int a = 0;
                while (a <= lot_.fetchItemNO())
                {
                    // Search for the next template in the Space
                    U1764486_Lot template = new U1764486_Lot(a++ + 1, null, null, null, null, null, null);
                    // If the object exists in the space
                    U1764486_Lot latestLot = (U1764486_Lot) space.readIfExists(template, null, 1500);
                    // Add any existing lots to the tables
                    if (latestLot != null) {
                        System.out.println("np previous lots");

                        lots.add(latestLot);
                        //System.out.println("l9000" + latestLot);
                        model.addRow(latestLot.SetArray());
                        //ap.FetchModel().fireTableDataChanged();

                    }System.out.println("MAINCLASS ENDED");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


}
