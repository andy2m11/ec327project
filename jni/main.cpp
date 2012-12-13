#include <jni.h>
#include "gamelogic.h"
#include <cstddef>
//put functions here


bool C_isValid( int x, int y, int** board, int player)
{
                //checks if current position is a valid move
    bool Valid;

    Valid = isEmpty(x,y,board) && ( (canConquerUp(x, y, player, board))        ||
                                   (canConquerDown(x, y, player, board))    ||
                                  (canConquerLeft(x, y, player, board))    ||
                                   (canConquerRight(x, y, player, board))     ||
                                   (canConquerDL(x, y, player, board))        ||
                                   (canConquerDR(x, y, player, board))        ||
                                   (canConquerUL(x, y, player, board))     ||
                                   (canConquerUR(x, y, player, board))    );

            //if spot is empty and any of the conquers return true, then it is valid
    return Valid;

}

//=======================================================================================
bool isEmpty(int x, int y, int** board)
{
    //If spot is empty, not valid
    if(board[x][y] == 0)    //val 2 = empty
    {      return true;        }
    else
    {      return false;        }

}
//=======================================================================================
int op(int player)
{    //Inverts player to make it simpler for checkline to check sandwhich states.
    if        (player==1)
    {        return 2;    }
    else if (player == 2)
    {        return 1;    }

}

//=======================================================================================
int checkLine(int player,int initialColumn,int initialRow,int directionX,int directionY,int** board)
{
    //cline reports value of last location of checkLine
    int cline = player;     //input is passed through OP function which player is actually opponent
                            //value.
    bool resolved = false;  //false as long as there are more conversions possible

    int x = initialColumn;
    int y = initialRow;
    while (!resolved)    //will continue in direction of given inputs as long as next piece
    {                    //is an enemy piece. if Next piece is not enemy piece then it has
                        //reached the end of possible conquers

        x += directionX;
        y += directionY;

        // Conditional to keep checkline on the board
        if (x >= 0 && x < 8 && y >= 0 && y < 8) {

            if (board[x][y] != player)    //If current position is players own piece
            {                            //then cline returns player's own value
                resolved = true;        //confirming a "sandwhich" or possible conquer
                cline = board[x][y];
            }
        }

        else
        {
     //
            resolved = true;    //cline = player here means it has reached end of board
        }
    }

    return cline;    //cline = 2 means it ended on empty space
}
//=======================================================================================

bool canConquerDL(int x, int y,int player, int** board)
{
    bool check = false;
    // conquer needs a minimum of 2 spaces to conquer so the edge cases(DL) will be false.

    if (x > 1 && y < 6 )
    {
        if (board[x-1][y+1] == op(player))    //checks if DL position is an enemy
        {    //calls checkline function to see if conversion in current direction leads to
            //a sandwhich state.

            int value = checkLine(op(player), (x-1), (y+1) , -1, +1, board);
            check = value == player; //if end location of checkline = to player's own
        }                             //piece then it is conquerable. Else, not conquerable
    }
    return check;
}

//=======================================================================================

bool canConquerDown(int x, int y,int player, int** board)
{
    bool check = false;
    // conquer needs a minimum of 2 spaces to conquer so the edge cases(D) will be false.

    if (y <6 )
    {
        if (board[x][y+1] == op(player)) //checks if down position is an enemy
        {        //calls checkline function to see if conversion in current direction leads to
                //a sandwhich state.

            int value = checkLine(op(player), x, (y+1) , 0, 1, board);
            check = value ==player;     //if end location of checkline = to player's own piece
                                     //then it is conquerable. Else, not conquerable
        }

    }

    return check;
}


//=======================================================================================
bool canConquerDR(int x, int y,int player, int** board)
{
    bool check = false;
    // conquer needs a minimum of 2 spaces to conquer so the edge cases(DR) will be false.
    if (x < 6 && y < 6 )
    {
        if (board[x+1][y+1] == op(player)) //checks DR
        {
            int value = checkLine(op(player), (x+1), (y+1) ,1, 1, board);
            check = value ==player;
        }
    }
    return check;
}

//=======================================================================================

bool canConquerLeft(int x, int y,int player, int** board)
{
    bool check = false;
    //min space condition
    if (x > 1 )
    {
        if (board[x-1][y] == op(player)) //checks left
        {
            int value = checkLine(op(player), (x-1), y ,-1,0, board);
            check = value == player;
        }
    }
    return check;
}

//=======================================================================================

bool canConquerRight(int x, int y,int player, int** board)
    {
    bool check = false;
     //min space condition
    if (x < 6 )
    {
        if (board[x+1][y] == op(player)) //checks right
        {
            int value = checkLine(op(player), (x+1), y ,1,0, board);
            check = value == player;
        }
    }
    return check;
}


//=======================================================================================
bool canConquerUL(int x, int y,int player, int** board)
{
    bool check = false;
    // impossible to conquer UL if space is less than 2
    if (x > 1 && y > 1 )
    {
        if (board[x-1][y-1] == op(player))     //check UL
        {
            int value = checkLine(op(player), (x-1), (y-1) ,-1, -1, board);
            check = value == player;
        }
    }
    return check;
}



//=======================================================================================

bool canConquerUp(int x, int y,int player, int** board)
{
    bool check = false;
    // impossible to conquer UP if space is less than 2
    if (y > 1)
    {
        if (board[x][y-1] == op(player))     //checks up
        {
            int value = checkLine( op(player), x, (y -1) ,0,-1, board);
            check = (value==player);
        }
    }
    return check;
}

//=======================================================================================
bool canConquerUR(int x, int y,int player, int** board)
{
    bool check = false;
    // impossible to conquer UR if space is less than 2
    if (x < 6 && y > 1 )
    {
        if (board[x+1][y-1] == op(player))//checks UR
        {
            int value = checkLine(op(player), (x+1), (y-1) ,1, -1, board);
            check = (value ==player);
        }
    }
    return check;
}

//=======================================================================================

int Conquer(int x, int y, int directionX, int directionY, int** board,int player)
{
    int X = x;        //use temp variables to store current conquer location
    int Y = y;

    bool ownChip = false;

    while ( !ownChip )    //Conquers until it reaches a friendly piece
    {
        //direction it conquers in
        X += directionX;
        Y += directionY;
        //if is not an own chip
        if (board[X][Y] == 0)
        {	ownChip = true;	}

        if ((board[X][Y] != player) /* &&(board[X][Y] != 0)*/ )
        {
            board[X][Y] = player;    //convert values
        }

        else
        {
            ownChip = true;            //end of conquer
        }
    }
}
//=======================================================================================

void C_fixBoard(int x, int y,  int** board, int player)
{
    //Checks the 8 canConquer results. If it returned a value of true
    //then fixBoard calls conquer to flip pieces in those directions

    if(canConquerUp(  x,  y, player, board ))
        {    Conquer( x,  y, 0, -1,  board, player);    }

    if(canConquerDown(  x,  y, player, board ))
        {    Conquer( x,  y, 0, 1,  board, player);    }

    if(canConquerLeft(  x,  y, player, board ))
        {    Conquer( x,  y, -1, 0,  board, player);    }

    if(canConquerRight(  x,  y, player, board ))
        {    Conquer( x,  y, 1, 0,  board, player);    }

    if(canConquerDL(  x,  y, player, board ))
        {    Conquer( x,  y, -1, 1,  board, player);    }

    if(canConquerDR(  x,  y, player, board ))
        {    Conquer( x,  y, 1, 1,  board, player);    }

    if(canConquerUL(  x,  y, player, board ))
        {    Conquer( x,  y, -1, -1,  board, player);}

    if(canConquerUR(  x,  y, player, board ))
        {    Conquer( x,  y, 1, -1,  board, player);    }
}


//=======================================================================================
extern "C"{



    JNIEXPORT jboolean JNICALL
    Java_com_ec327cassio_reversi_MainActivity_isValid(JNIEnv * env, jobject obj, jint x,
    jint y, jobjectArray board, jint player)
    {
        int len1 = 8;
        int **the_c_board;
        the_c_board = new int*[len1];
        //populate
        for (int i =0; i < 8; i++) {
            jintArray row = (jintArray)env->GetObjectArrayElement(board,i);
            jint *element = env->GetIntArrayElements(row,0);
            //allocate the sub-array for the row
            the_c_board[i] = new int[len1];
            for (int j=0; j<8; j++) {
                the_c_board[i][j] = element[j];
            }
        }
        bool result = C_isValid((int)x,(int) y,the_c_board,(int)player);


        //properly delete the array before returning bool
        for (int k=0; k<8; k++) {
            delete [] the_c_board[k];
        }
     delete [] the_c_board;
        return (jboolean) result;

    }

    JNIEXPORT jobjectArray JNICALL
    Java_com_ec327cassio_reversi_MainActivity_FixBoard(JNIEnv * env, jobject obj, jint x,
    jint y, jobjectArray board, jint player)
    {
        int len1 = 8;
                int** the_c_board;
                the_c_board = new int*[len1];
                //populate
                for (int i =0; i < 8; i++) {
                    jintArray row = (jintArray)env->GetObjectArrayElement(board,i);
                    jint *element = env->GetIntArrayElements(row,0);
                    //allocate the sub-array for the row
                    the_c_board[i] = new int[len1];
                    for (int j=0; j<8; j++) {
                        the_c_board[i][j] = element[j];
                    }
                }

                C_fixBoard( (int) x, (int) y, the_c_board, (int)player );
                jclass intArrayClass = env->FindClass("[I");


                jobjectArray myReturnable2DArray = env->NewObjectArray((jsize) 8,  intArrayClass,NULL);

                    // Go through the firs dimension and add the second dimension arrays
                    for (unsigned int p = 0; p < 8; p++)
                    {
                        jintArray intArray = env->NewIntArray(8);
                        env->SetIntArrayRegion(intArray, (jsize) 0, (jsize) 8, (jint*) the_c_board[p]);
                        env->SetObjectArrayElement(myReturnable2DArray, (jsize) p, intArray);
                        env->DeleteLocalRef(intArray);
                    }



                //properly delete the array before returning
            for (int k=0; k<8; k++) {
                    delete [] the_c_board[k];
                }
                delete [] the_c_board;

                return myReturnable2DArray;


    }


}
/*android.mk
 *
 * LOCAL_SRC_FILES += isValid.cpp
LOCAL_SRC_FILES += ConquerDirection.cpp
LOCAL_SRC_FILES += checkLine.cpp
LOCAL_SRC_FILES += op.cpp
LOCAL_SRC_FILES += isEmpty.cpp
LOCAL_SRC_FILES += Conquer.cpp
LOCAL_SRC_FILES += canConquerUp.cpp
LOCAL_SRC_FILES += canConquerDown.cpp
LOCAL_SRC_FILES += canConquerLeft.cpp
LOCAL_SRC_FILES += canConquerRight.cpp
LOCAL_SRC_FILES += canConquerDL.cpp
LOCAL_SRC_FILES += canConquerDR.cpp
LOCAL_SRC_FILES += canConquerUL.cpp
LOCAL_SRC_FILES += canConquerUR.cpp
 *
 */
