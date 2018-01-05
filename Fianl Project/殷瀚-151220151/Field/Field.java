package Field;

import Characters.*;
import Layout.Queue;
import Layout.QuickSorter;
import Layout.Troop;
import Types.COLOR;
import Types.FormationName;
import Types.SENIORITY;
import Types.TianGan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Field extends JPanel{
    private int fieldWidth, fieldHeight, positionRowCount, positionColCount, troopNum;
    private Position[][] positions;

    private List<Troop> troops = new ArrayList<>();

    private Image backgroundImage;
    private Image positionSquareImage;

    private boolean isGridViewVisible = true;

    public Field(int fieldWidth, int fieldHeight, int positionRowCount, int positionColCount, int troopNum) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.positionRowCount = positionRowCount;
        this.positionColCount = positionColCount;
        this.troopNum = troopNum;

        positions = new Position[positionRowCount][positionColCount];
        for(int i = 0; i < positionRowCount; i++) {
            for (int j = 0; j < positionColCount; j++) {
                positions[i][j] = new Position(i, j, this);
            }
        }

        URL loc1 = this.getClass().getClassLoader().getResource("Image/background.jpg");
        backgroundImage = new ImageIcon(loc1).getImage();

        URL loc2 = this.getClass().getClassLoader().getResource("Image/square.png");
        positionSquareImage = new ImageIcon(loc2).getImage();

        addKeyListener(new TAdapter());
        setFocusable(true);
    }
    public Field(int fieldWidth, int troopNum){
        this.fieldWidth = fieldWidth;
        this.troopNum = troopNum;

        positions = new Position[fieldWidth][fieldWidth];

        for(int i = 0; i < fieldWidth; i++){
            for(int j = 0; j < fieldWidth; j++)
                positions[i][j] = new Position(i, j, this);
        }
    }

    public Position[][] getPositions(){
        return this.positions;
    }
    public void addTroop(Troop newTroop){
        newTroop.enterField(this);
        troops.add(newTroop);
    }


    public int getBoardWidth() {
        return this.fieldWidth;
    }

    public int getBoardHeight() {
        return this.fieldHeight;
    }

    private void buildWorld(Graphics g) {
        // background
        g.drawImage(backgroundImage, 0, 0, this.getBoardWidth(), this.getBoardHeight(), this);

        int positionWidth = fieldWidth/positionColCount, positionHeight = fieldHeight/positionRowCount;
        if (isGridViewVisible) {
 /*           g.drawImage(positionSquareImage,
                    0*positionWidth, 0*positionHeight,
                    positionWidth, positionHeight,
                    this);
            g.drawImage(positionSquareImage,
                    1*positionWidth, 0*positionHeight,
                    positionWidth, positionHeight,
                    this);
            g.drawImage(positionSquareImage,
                    0*positionWidth, 1*positionHeight,
                    positionWidth, positionHeight,
                    this);
*/
            for(int i = 0; i < positionRowCount; i++) {
                for(int j = 0; j < positionColCount; j++) {
                    g.drawImage(positionSquareImage,
                            j*positionWidth, i*positionHeight,
                            positionWidth, positionHeight,
                            this);
                }
            }

        }
        for(Troop troop: troops) {
            troop.paintInGraphics(g, positionWidth, positionHeight);
        }

    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        buildWorld(g);
    }
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            switch (key) {
                case KeyEvent.VK_LEFT: break;
                case KeyEvent.VK_SPACE:
                    for(Troop troop: troops) {
                        troop.setFormation(FormationName.锋矢);
                    }
                    break;
                case KeyEvent.VK_S:
                    for(Troop troop: troops) {
                        troop.startActing();
                    }
                    break;
                default: break;
            }

            repaint();
        }
    }

    public void rawShow(){ //  依次显示每个position
        System.out.println();
        for(int i = 0; i < fieldWidth; i++){
            for(int j = 0; j < fieldWidth; j++){
                if(positions[i][j].getHolder() == null){
                    System.out.print("_________ ");
                }else{
                    System.out.print(positions[i][j].getHolder().toString());
                }
            }
            System.out.println();
        }
        System.out.println();

        act();
    }

    public void act(){
        for(int i = 0 ; i < troops.size() ; i++) {
            troops.get(i).act();
        }
    }

    public static void main(String[] args) {
        /* 初始化所有生物 */
        //葫芦娃
        Huluwa[] HuluBrothers = new Huluwa[7];
        for (int i = 0; i < HuluBrothers.length; i++) {
            HuluBrothers[i] = new Huluwa(COLOR.values()[i], SENIORITY.values()[i]);
        }
        // 爷爷
        Cheerer YeYe = new Cheerer("爷爷");
        // 蛇精
        Cheerer SheJing = new Cheerer("蛇精");
        // 小喽啰
        Louluo[] lackeys = new Louluo[10];
        for (int i = 0; i < lackeys.length; i++) {
            lackeys[i] = new Louluo(TianGan.values()[i]);
        }
        // 蝎子精
        Leader XieZiJing = new Leader("蝎子精");


        /* 初始化各方势力 */
        Troop powerOfHuluwa = new Troop("葫芦娃", 0, -1);
        Troop powerOfYaojing = new Troop("妖精", 0, 4);

        /* 所有人物加入势力 */
        powerOfHuluwa.addCreatures(HuluBrothers);
        powerOfHuluwa.addOneCreature(YeYe);

        powerOfYaojing.addCreatures(lackeys);
        powerOfYaojing.addOneCreature(SheJing);
        powerOfYaojing.addOneCreature(XieZiJing);

        /* 初始化场地，11*11方阵，可容纳2方势力 */
        Field field = new Field(11, 2);

        /* 各方势力登场 */
        field.addTroop(powerOfHuluwa);
        field.addTroop(powerOfYaojing);



        /* 各方势力布阵 */
        powerOfHuluwa.setFormation(FormationName.长蛇);
        powerOfYaojing.setFormation(FormationName.锋矢);

        /* 使用queue管理待排序的成员 */
        Queue queue = new Queue(HuluBrothers);

        queue.shuffle();    //  打乱
        field.rawShow();

        new QuickSorter().sort(queue); //  冒泡排序
        field.rawShow();

        /* 妖精变换阵型 */
        powerOfYaojing.setFormation(FormationName.偃月);

        field.rawShow();

    }


}
