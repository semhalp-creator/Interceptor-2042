// Name: Semih Alp Yalcin
// ID: 2024400105

public class Main {

   static  int W = 1200;
   static  int H = 800;

    //player
    static double playerX=600;
    static double playerY =200;
    static double speed =20.0;
    static int pauseDuration= 50;
    static int lives= 3;
    static int score= 0;

    // keys
    static int keyLeft = 37;
    static int keyRight= 39;
    static int keyUp =38;
    static int keyDown= 40;
    static int keySpace = 32;
    static int keyEnter = 10;
    static int key_P = 80;
    static int key_Q = 81;
    static int key_E = 69;
    static int key_A = 65;
    static int key_D = 68;

    //bullets
    static double[] bulletX = new double[20];
    static double[] bulletY = new double[20];
    static boolean[] bulletActive = new boolean[20];

    // enemy
    static double[] enemyX = new double[6];
    static double[] enemyY = new double[6];
    static boolean[] enemyAlive = new boolean[6];
    static double enemySpeed = 3.0;
    static int enemyDir = 1;

    // enemy bullets
    static double[] isBulletX = new double[10];
    static double[] isBulletY = new double[10];
    static boolean[] isBulletActive = new boolean[10];

    // Explosion
    static double[] expX = new double[6];
    static double[] expY = new double[6];
    static int[] expTimer = new int[6];

    // Heart drop
    static double[] heartX = new double[6];
    static double[] heartY = new double[6];
    static boolean[] heartActive = new boolean[6];

    public static void main(String[] args) {

        StdDraw.setCanvasSize(W, H);
        StdDraw.setXscale(0, W);
        StdDraw.setYscale(0, H);
        StdDraw.setTitle("2042: Interceptor");
        StdDraw.enableDoubleBuffering();


        //main menu loop

        while (true) {
            StdDraw.picture(W / 2.0, H / 2.0, "assets/background.png", W, H);
            StdDraw.picture(W / 2.0, H * 0.75, "assets/title.png", 700, 200);

            StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(W / 2.0, H * 0.55, "> Start Game <");

            StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 25));
            StdDraw.text(W / 2.0, H * 0.52, "FPS: ~" + (1000 / pauseDuration) + " | Speed: " + speed);

            //keyboard control
            if (StdDraw.isKeyPressed(keyLeft)) playerX -= speed;
            if (StdDraw.isKeyPressed(keyRight)) playerX += speed;
            if (StdDraw.isKeyPressed(keyUp)) playerY += speed;
            if (StdDraw.isKeyPressed(keyDown)) playerY -= speed;
            if (playerX < 50) playerX = 50;
            if (playerX > W - 50) playerX = W - 50;
            if (playerY < 50) playerY = 50;
            if (playerY > H - 50) playerY = H - 50;

            // Q= speed up E → speed down
            if (StdDraw.isKeyPressed(key_Q)) speed += 1.0;
            if (StdDraw.isKeyPressed(key_E)) speed -= 1.0;
            if (speed < 1.0) speed = 1.0;

            // A means increasing FPS, D means deacreasing fps
            if (StdDraw.isKeyPressed(key_A)) pauseDuration -= 5;
            if (StdDraw.isKeyPressed(key_D)) pauseDuration += 5;
            if (pauseDuration < 10) pauseDuration = 10;
            if (pauseDuration > 200) pauseDuration = 200;

            // space= fire
            if (StdDraw.isKeyPressed(keySpace)) {
                for (int i = 0; i < bulletActive.length; i++) {
                    if (!bulletActive[i]) {
                        bulletActive[i] = true;
                        bulletX[i] = playerX;
                        bulletY[i] = playerY + 60;
                        break;
                    }
                }
            }

            // moves bullets and draw
            for (int i = 0; i < bulletActive.length; i++) {
                if (bulletActive[i]) {
                    bulletY[i] += 20;
                    if (bulletY[i] > H) bulletActive[i] = false;
                    StdDraw.picture(bulletX[i], bulletY[i], "assets/bullet.png", 15, 40);
                }
            }

            // draw interceptor
            StdDraw.picture(playerX, playerY, "assets/interceptor.png", 100, 120);

            // infos
            StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 20));
            StdDraw.text(W / 2.0, 120, "Move: [<-][^][v][->]");
            StdDraw.text(W / 2.0, 90, "Shoot: [Space]");
            StdDraw.text(W / 2.0, 60, "Press [ENTER] to start");
            StdDraw.text(W / 2.0, 30, "FPS: A/D    Speed: Q/E");

            StdDraw.show();
            StdDraw.pause(pauseDuration);

            // ENTER, go play
            if (StdDraw.isKeyPressed(keyEnter)) {
                break;
            }
        }


        //game loop
        while (true) {

            //reset
            lives = 3;
            score = 0;
            playerX = W / 2.0;
            playerY = 200;
            enemyDir = 1;
            for (int i = 0; i < 6; i++) {
                enemyX[i] = 150 + (i % 3) * 250;
                enemyY[i] = H - 150 - (i / 3) * 150;
                enemyAlive[i] = true;
                heartActive[i] = false;
                expTimer[i] = 0;
            }
            for (int i = 0; i < bulletActive.length; i++) bulletActive[i] = false;
            for (int i = 0; i < isBulletActive.length; i++) isBulletActive[i] = false;


            // in game loop

            while (true) {
                StdDraw.picture(W / 2.0, H / 2.0, "assets/background.png", W, H);

                // score and heart
                StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 30));
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.textLeft(10, H - 30, "Score: " + score);
                for (int i = 0; i < lives; i++) {
                    StdDraw.picture(W - 40 - i * 60, H - 40, "assets/heart.png", 50, 50);
                }

                // pause control
                if (StdDraw.isKeyPressed(key_P)) {
                    StdDraw.picture(W / 2.0, H / 2.0, "assets/paused.png", 600, 200);
                    StdDraw.show();
                    while (StdDraw.isKeyPressed(key_P)) StdDraw.pause(50);
                    while (!StdDraw.isKeyPressed(key_P)) StdDraw.pause(50);
                    while (StdDraw.isKeyPressed(key_P)) StdDraw.pause(50);
                }

                // player moves
                if (StdDraw.isKeyPressed(keyLeft)) playerX -= speed;
                if (StdDraw.isKeyPressed(keyRight)) playerX += speed;
                if (StdDraw.isKeyPressed(keyUp)) playerY += speed;
                if (StdDraw.isKeyPressed(keyDown)) playerY -= speed;
                if (playerX < 50) playerX = 50;
                if (playerX > W - 50) playerX = W - 50;
                if (playerY < 50) playerY = 50;
                if (playerY > H - 50) playerY = H - 50;

                // player fire
                if (StdDraw.isKeyPressed(keySpace)) {
                    for (int i = 0; i < bulletActive.length; i++) {
                        if (!bulletActive[i]) {
                            bulletActive[i] = true;
                            bulletX[i] = playerX;
                            bulletY[i] = playerY + 60;
                            break;
                        }
                    }
                }
                for (int i = 0; i < bulletActive.length; i++) {
                    if (bulletActive[i]) {
                        bulletY[i] += 20;
                        if (bulletY[i] > H) bulletActive[i] = false;
                        StdDraw.picture(bulletX[i], bulletY[i], "assets/bullet.png", 15, 40);
                    }
                }

                // enemies movement
                boolean hitWall = false;
                for (int i = 0; i < 6; i++) {
                    if (enemyAlive[i]) {
                        enemyX[i] += enemySpeed * enemyDir;
                        if (enemyX[i] > W - 100 || enemyX[i] < 100) hitWall = true;
                    }
                }
                if (hitWall) {
                    enemyDir *= -1;
                    for (int i = 0; i < 6; i++) {
                        if (enemyAlive[i]) enemyY[i] -= 30;
                    }
                }
                for (int i = 0; i < 6; i++) {
                    if (enemyAlive[i]) {
                        StdDraw.picture(enemyX[i], enemyY[i], "assets/enemyFighter.png", 80, 80);
                    }
                }

                //enemies bullets
                for (int i = 0; i < 6; i++) {
                    if (enemyAlive[i] && Math.random() < 0.005) {
                        for (int j = 0; j < isBulletActive.length; j++) {
                            if (!isBulletActive[j]) {
                                isBulletActive[j] = true;
                                isBulletX[j] = enemyX[i];
                                isBulletY[j] = enemyY[i] - 40;
                                break;
                            }
                        }
                    }
                }
                for (int i = 0; i < isBulletActive.length; i++) {
                    if (isBulletActive[i]) {
                        isBulletY[i] -= 15;
                        if (isBulletY[i] < 0) isBulletActive[i] = false;
                        StdDraw.picture(isBulletX[i], isBulletY[i], "assets/enemyBullet.png", 15, 40);
                    }
                }

                // player plane
                StdDraw.picture(playerX, playerY, "assets/interceptor.png", 100, 120);

                // player bullet and enemy crush
                for (int i = 0; i < bulletActive.length; i++) {
                    if (!bulletActive[i]) continue;
                    for (int j = 0; j < 6; j++) {
                        if (!enemyAlive[j]) continue;
                        if (Math.abs(bulletX[i] - enemyX[j]) < 47 &&
                                Math.abs(bulletY[i] - enemyY[j]) < 60) {
                            bulletActive[i] = false;
                            enemyAlive[j] = false;
                            score += 30;
                            expX[j] = enemyX[j];
                            expY[j] = enemyY[j];
                            expTimer[j] = 10;
                            if (Math.random() < 0.3) {
                                heartActive[j] = true;
                                heartX[j] = enemyX[j];
                                heartY[j] = enemyY[j];
                            }
                        }
                    }
                }

                // enemy bullet and player crush
                for (int i = 0; i < isBulletActive.length; i++) {
                    if (!isBulletActive[i]) continue;
                    if (Math.abs(isBulletX[i] - playerX) < 57 &&
                            Math.abs(isBulletY[i] - playerY) < 80) {
                        isBulletActive[i] = false;
                        lives--;
                    }
                }

                // enemy and player crush
                for (int i = 0; i < 6; i++) {
                    if (!enemyAlive[i]) continue;
                    if (Math.abs(enemyX[i] - playerX) < 90 &&
                            Math.abs(enemyY[i] - playerY) < 100) {
                        enemyAlive[i] = false;
                        lives--;
                    }
                }

                //explosion
                for (int i = 0; i < 6; i++) {
                    if (expTimer[i] > 5) {
                        StdDraw.picture(expX[i], expY[i], "assets/explosionSmall.png", 80, 80);
                        expTimer[i]--;
                    } else if (expTimer[i] > 0) {
                        StdDraw.picture(expX[i], expY[i], "assets/explosionBig.png", 120, 120);
                        expTimer[i]--;
                    }
                }

                // Heart drop
                for (int i = 0; i < heartActive.length; i++) {
                    if (heartActive[i]) {
                        heartY[i] -= 3;
                        StdDraw.picture(heartX[i], heartY[i], "assets/heart.png", 40, 40);
                        if (Math.abs(heartX[i] - playerX) < 70 &&
                                Math.abs(heartY[i] - playerY) < 80) {
                            heartActive[i] = false;
                            if (lives < 3) lives++;
                        }
                        if (heartY[i] < 0) heartActive[i] = false;
                    }
                }

                // game over control
                if (lives <= 0) break;

                //victory control
                boolean allDead = true;
                for (int i = 0; i < 6; i++) {
                    if (enemyAlive[i]) { allDead = false; break; }
                }
                if (allDead) break;

                StdDraw.show();
                StdDraw.pause(pauseDuration);
            }


            //end game screen
            boolean victory = true;
            for (int i = 0; i < 6; i++) {
                if (enemyAlive[i]) { victory = false; break; }
            }

            int selected = 0;

            while (true) {
                StdDraw.picture(W / 2.0, H / 2.0, "assets/background.png", W, H);

                if (victory) {
                    StdDraw.picture(W / 2.0, H * 0.7, "assets/victory.png", 700, 200);
                } else {
                    StdDraw.picture(W / 2.0, H * 0.7, "assets/gameOver.png", 700, 200);
                }

                StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(W / 2.0, H * 0.5, "Score: " + score);

                if (selected == 0) {
                    StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
                    StdDraw.text(W / 2.0, H * 0.43, "> Restart <");
                    StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 40));
                    StdDraw.text(W / 2.0, H * 0.37, "End Game");
                } else {
                    StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 40));
                    StdDraw.text(W / 2.0, H * 0.43, "Restart");
                    StdDraw.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 40));
                    StdDraw.text(W / 2.0, H * 0.37, "> End Game <");
                }

                if (StdDraw.isKeyPressed(keyUp)) {
                    selected = 0;
                    while (StdDraw.isKeyPressed(keyUp)) StdDraw.pause(50);
                }
                if (StdDraw.isKeyPressed(keyDown)) {
                    selected = 1;
                    while (StdDraw.isKeyPressed(keyDown)) StdDraw.pause(50);
                }

                if (StdDraw.isKeyPressed(keyEnter)) {
                    if (selected == 1) {
                        System.exit(0);
                    } else {
                        break; // restart
                    }
                }

                StdDraw.show();
                StdDraw.pause(pauseDuration);
            }

        }
    }
}