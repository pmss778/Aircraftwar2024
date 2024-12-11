package com.example.aircraftwar2024.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.annotation.NonNull;

import com.example.aircraftwar2024.GameRecord.GameRecord;
import com.example.aircraftwar2024.GameRecord.GameRecordDAOImpl;
import com.example.aircraftwar2024.ImageManager;
import com.example.aircraftwar2024.activity.GameActivity;
import com.example.aircraftwar2024.activity.OnlineOverActivity;
import com.example.aircraftwar2024.aircraft.AbstractAircraft;
import com.example.aircraftwar2024.aircraft.AbstractEnemyAircraft;
import com.example.aircraftwar2024.aircraft.BossEnemy;
import com.example.aircraftwar2024.aircraft.HeroAircraft;
import com.example.aircraftwar2024.basic.AbstractFlyingObject;
import com.example.aircraftwar2024.bullet.AbstractBullet;
import com.example.aircraftwar2024.factory.enemy_factory.BossFactory;
import com.example.aircraftwar2024.factory.enemy_factory.EliteFactory;
import com.example.aircraftwar2024.factory.enemy_factory.EnemyFactory;
import com.example.aircraftwar2024.factory.enemy_factory.MobFactory;
import com.example.aircraftwar2024.music.MyMediaPlayer;
import com.example.aircraftwar2024.music.MySoundPool;
import com.example.aircraftwar2024.supply.AbstractFlyingSupply;
import com.example.aircraftwar2024.supply.BombSupply;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 游戏逻辑抽象基类，遵循模板模式，action() 为模板方法
 * 包括：游戏主面板绘制逻辑，游戏执行逻辑。
 * 子类需实现抽象方法，实现相应逻辑
 * @author hitsz
 */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable{
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String playerId;

    public static final String TAG = "BaseGame";
    boolean isonline;
    int opponentScore;
    public Handler mhandler;
    boolean mbLoop; //控制绘画线程的标志位
    private final SurfaceHolder mSurfaceHolder;
    private Canvas canvas;  //绘图的画布
    private final Paint mPaint;
    private final String user = "test";
    private String Level;

    private boolean musicFlag = false;
    private MyMediaPlayer myMediaPlayer;
    private MySoundPool mySoundPool;


    //点击屏幕位置
    float clickX = 0, clickY=0;

    private int backGroundTop = 0;

    /**
     * 背景图片缓存，可随难度改变
     */
    protected Bitmap backGround;

    /**
     * 周期（ms)
     * 控制英雄机射击周期，默认值设为简单模式
     */
    protected double heroShootCycle = 10;
    private int heroShootCounter = 0;

    protected int tickCycle = Integer.MAX_VALUE;
    protected int tickCounter = 0;

    /**
     * 周期（ms)
     * 控制敌机射击周期，默认值设为简单模式
     */
    protected double enemyShootCycle = 20;
    private int enemyShootCounter = 0;

    /**
     * 游戏难度，敌机血量和速度的提升倍率，
     * 普通和困难模式随着时间增加而提高难度，提升普通和精英敌机的速度和血量
     */
    protected double gameLevel = 1.0;


    /**
     * 普通和困难模式中
     * 当得分每超过一次bossScoreThreshold，则产生一次boss机
     */
    protected int bossScoreThreshold = Integer.MAX_VALUE;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private final int timeInterval = 4;

    private final HeroAircraft heroAircraft;

    protected final List<AbstractEnemyAircraft> enemyAircrafts;
    private final List<AbstractFlyingSupply> flyingSupplies;

    private final List<AbstractBullet> heroBullets;
    private final List<AbstractBullet> enemyBullets;

    protected int enemyMaxNumber = 5;

    private boolean gameOverFlag = false;
    private int score = 0;


    /**
     * 周期（ms)
     * 控制英雄机射击周期，默认值设为简单模式
     */
    private final int cycleDuration = 20;
    private int cycleTime = 0;
    protected double bossLevel = 1.0;

    /**
     * 产生小敌机和精英敌机。
     * 返回产生的敌机，可以为空链表（即表示不产生）；不能返回null！
     */
    private int enemyCounter = 0;
    protected double enemyCycle = 20;
    /**
     * 控制精英机产生概率，默认值设为简单模式
     */
    protected double eliteProb = 0.3;
    /**
     * 敌机工厂
     */
    private final EnemyFactory mobEnemyFactory;
    private final EnemyFactory eliteEnemyFactory;
    private final EnemyFactory bossEnemyFactory;
    private final Random random = new Random();
    public Context context;

    public BaseGame(Context context, Handler mhandler){
        super(context);

        this.context = context;
        this.mhandler = mhandler;
        myMediaPlayer = new MyMediaPlayer(context);
        mySoundPool = new MySoundPool(context);
        mbLoop = true;
        mPaint = new Paint();  //设置画笔

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        ImageManager.initImage(context);

        // 初始化英雄机
        heroAircraft = HeroAircraft.getHeroAircraft();
        heroAircraft.setHp(1000);

        enemyAircrafts = new CopyOnWriteArrayList<>();
        heroBullets = new CopyOnWriteArrayList<>();
        enemyBullets = new CopyOnWriteArrayList<>();
        flyingSupplies = new CopyOnWriteArrayList<>();

        mobEnemyFactory = new MobFactory();
        eliteEnemyFactory = new EliteFactory();
        bossEnemyFactory = new BossFactory();

        heroController();

    }
    public void setLevel(String level){
        this.Level = level;
    }
    private void heroShootAction() {
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }
    private void enemyShootAction() {
        // 敌机射击
        for (AbstractEnemyAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
    }

    /**
     音乐播放
     */
    public void playBgm() {
        myMediaPlayer.playBgm();
    }

    public void stopBgm() {
        myMediaPlayer.stopBgm();
    }

    public void playBossBgm() {
        myMediaPlayer.playBossBgm();
    }

    public void stopBossBgm() {
        myMediaPlayer.stopBossBgm();
    }
    public void playBulletHit() {
        mySoundPool.playSound(MySoundPool.SOUND_BULLET_HIT);
    }

    public void playBombExplosion() {
        mySoundPool.playSound(MySoundPool.SOUND_BOMB_EXPLOSION);
    }

    public void playGetSupply() {
        mySoundPool.playSound(MySoundPool.SOUND_GET_SUPPLY);
    }

    public void playGameOver() {
        mySoundPool.playSound(MySoundPool.SOUND_GAME_OVER);
    }


    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {


        if (musicFlag){
            playBgm();
        }

        //new Thread(new Runnable() {
        Runnable task = () -> {

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                // produceBoss 根据游戏难度策略产生 BOSS
                //根据游戏设定，随着时间提高游戏难度
                tick();
                enemyAircrafts.addAll(produceBoss());

                // produceEnemy 根据游戏难度策略产生精英敌机和小敌机
                enemyAircrafts.addAll(produceEnemy());
                if(shouldHeroShoot()){
                    heroShootAction();
                }

                if(shouldEnemyShoot()){
                    enemyShootAction();
                }

            }

            // 子弹移动
            bulletsMoveAction();
            // 飞机移动
            aircraftsMoveAction();

            suppliesMoveAction();

            // 撞击检测
            try {

                crashCheckAction();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 后处理
            try {
                postProcessAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        task.run();
    }
    /**
     * 每个时刻均调用一次。
     * 普通和困难模式随着时间增加会提高游戏难度
     */
    protected abstract void tick();
    private void suppliesMoveAction() {
        for (AbstractFlyingSupply flyingSupply : flyingSupplies) {
            flyingSupply.forward();
        }
    }
    protected List<AbstractEnemyAircraft> produceBoss() {
        List<AbstractEnemyAircraft> res = new LinkedList<>();

        //当得分每超过一次bossScoreThreshold，且当前无boos机存在，则产生一次boss机
        // 普通模式boss机的血量不会变化
        if (this.getScore() >= bossScoreThreshold && !this.existBoss()) {
            bossScoreThreshold += bossScoreThreshold;
            res.add(bossEnemyFactory.createEnemyAircraft(bossLevel));
            if (musicFlag){
                stopBgm();
                playBossBgm();
                Log.i(TAG,"START PLAYING BOSSGBM");
            }
        }
        return res;
    }
    public int getScore() {
        return score;
    }
    private boolean existBoss() {
        for (AbstractEnemyAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft instanceof BossEnemy) {
                return true;
            }
        }
        return false;
    }


    private List<AbstractEnemyAircraft> produceEnemy() {
        enemyCounter++;
        if (enemyCounter >= enemyCycle) {
            // 每当计数次数达到周期，重置计数器，执行相关指令
            // 后射击周期同理
            enemyCounter = 0;
            List<AbstractEnemyAircraft> res = new LinkedList<>();

            // 产生敌机
            if (enemyAircrafts.size() < enemyMaxNumber) {
                if (random.nextDouble() < eliteProb) {
                    //精英机
                    res.add(eliteEnemyFactory.createEnemyAircraft(gameLevel));
                }
                else{
                    //普通敌机
                    res.add(mobEnemyFactory.createEnemyAircraft(gameLevel));
                }

            }
            return res;
        }
        return new LinkedList<>();
    }

    private boolean shouldHeroShoot() {
        heroShootCounter++;
        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            return true;
        }
        return false;
    }

    private boolean shouldEnemyShoot() {
        enemyShootCounter++;
        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;
            return true;
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void heroController(){
        setOnTouchListener((view, motionEvent) -> {
            clickX = motionEvent.getX();
            clickY = motionEvent.getY();
            heroAircraft.setLocation(clickX, clickY);

            if ( clickX<0 || clickX> GameActivity.screenWidth || clickY<0 || clickY>GameActivity.screenHeight){
                // 防止超出边界
                return false;
            }
            return true;
        });
    }


    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void bulletsMoveAction() {
        for (AbstractBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (AbstractBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() throws InterruptedException {
        // 敌机子弹攻击英雄
        for (AbstractBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
                if (musicFlag){
                    playBulletHit();
                }
            }
        }

        // 英雄子弹攻击敌机
        for (AbstractBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemyAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    if (musicFlag){
                        playBulletHit();
                    }

                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        //获得分数，产生道具补给
                        score += enemyAircraft.score();
                        if (isonline){
                            sendScoreToServer(score);
                        }
                        flyingSupplies.addAll(enemyAircraft.generateSupplies());
                        if (musicFlag){
                            if (enemyAircraft instanceof BossEnemy){
                                stopBossBgm();
                                playBgm();
                                Log.i(TAG,"STOP PLAYING BOSSGBM");
                            }
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得补给
        for (AbstractFlyingSupply flyingSupply : flyingSupplies) {
            if (flyingSupply.notValid()) {
                continue;
            }
            if (heroAircraft.crash(flyingSupply) || flyingSupply.crash(heroAircraft)) {
                flyingSupply.activate();
                flyingSupply.vanish();
                if (musicFlag){
                    if (flyingSupply instanceof BombSupply){
                        playBombExplosion();
                    }
                    else {
                        playGetSupply();
                    }
                }
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() throws IOException {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        flyingSupplies.removeIf(AbstractFlyingObject::notValid);

        if (heroAircraft.notValid()) {
            Log.i(TAG, "heroAircraft is not Valid");

            if (musicFlag){
                if (gameOverFlag == false)
                playGameOver();
            }
            gameOverFlag = true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!isonline){

                mbLoop = false;
                Date now = new Date();

                // 创建日期格式化对象，指定格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                // 将当前时间格式化为字符串
                String currentTime = sdf.format(now);

                GameRecord gameRecord = new GameRecord(score,user,currentTime);
                String filename = Level + "ModeRecord.txt"; // 文件名

                GameRecordDAOImpl gameRecordDAO = new GameRecordDAOImpl();
                gameRecordDAO.saveRecordToFile(context,filename,gameRecord);

                myMediaPlayer.release();
                mySoundPool.release();
                Message msg = Message.obtain();
                msg.what = 1;
                mhandler.sendMessage(msg);
            }

            else if (isonline){
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("DIE");
            }
        }

    }

    public void draw() {
        canvas = mSurfaceHolder.lockCanvas();
        if(mSurfaceHolder == null || canvas == null){
            return;
        }

        //绘制背景，图片滚动
        canvas.drawBitmap(backGround,0,this.backGroundTop-backGround.getHeight(),mPaint);
        canvas.drawBitmap(backGround,0,this.backGroundTop,mPaint);
        backGroundTop +=1;
        if (backGroundTop == GameActivity.screenHeight)
            this.backGroundTop = 0;

        //先绘制子弹，后绘制飞机
        paintImageWithPositionRevised(enemyBullets); //敌机子弹


        paintImageWithPositionRevised(heroBullets);  //英雄机子弹


        paintImageWithPositionRevised(enemyAircrafts);//敌机

        paintImageWithPositionRevised(flyingSupplies);//道具


        canvas.drawBitmap(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY()- ImageManager.HERO_IMAGE.getHeight() / 2,
                mPaint);

        //画生命值和分数
        paintScoreAndLife();
        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    private void paintImageWithPositionRevised(List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            Bitmap image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            canvas.drawBitmap(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, mPaint);
        }
    }

    private void paintScoreAndLife() {
        int x = 10;
        int y = 40;

        mPaint.setColor(Color.RED);
        mPaint.setTextSize(50);
        canvas.drawText("SCORE:" + this.score, x, y, mPaint);
        y = y + 60;
        canvas.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, mPaint);
        y = y + 60;
        if (isonline){
            canvas.drawText("Opponent SCORE:" + this.getOpponentScore(), x, y, mPaint);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mbLoop = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        GameActivity.screenWidth = i1;
        GameActivity.screenHeight = i2;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        /*TODO*/
        mbLoop = false;
    }

    @Override
    public void run() {
        /*TODO*/
        if (isonline){
            connectToServer();
            sendScoreToServer(score);
        }
        while(mbLoop){
            if (!isOver()){
                action();
            }
            draw();
        }
    }


    public void setMusicFlag(boolean musicFlag){
        this.musicFlag = musicFlag;
    }
    public boolean isOver(){
        return gameOverFlag;
    }
    public int getOpponentScore() {
        return opponentScore;
    }
    public void setIsonline(boolean isonline){
        this.isonline = isonline;
    }
    private void connectToServer() {
                                    new Thread(() -> {
                                        try {
                                            socket = new Socket("10.0.2.2", 9999);
                                            out = new PrintWriter(socket.getOutputStream(), true);
                                            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                                            String inputLine;
                                            while (true) {
                                                if ((inputLine = in.readLine()) != null){
                                                    if (inputLine.startsWith("Player")){
                                                        System.out.println("Recieved:"+inputLine);
                                                        if (!inputLine.startsWith(playerId)){
                                                            String[] parts = inputLine.split(":");
                                                            int value = Integer.parseInt(parts[1]);
                                                            opponentScore = value;
                                                            System.out.println("update opponentScore:"+value);
                            }
                        }
                        else if (inputLine.equals("END")){
                            gameOverFlag = true;
                            mbLoop = false;
                            int [] Result = {score,opponentScore};
                            myMediaPlayer.release();
                            mySoundPool.release();
                            Message msg = Message.obtain();
                            msg.what = 2;
                            msg.obj = Result;
                            mhandler.sendMessage(msg);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to server: " + e.getMessage());
            }
        }).start();
    }

    private void sendScoreToServer(int score) {
        if (out != null) {
            out.println(playerId + ":" + score);
        }
        else {
        }
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}