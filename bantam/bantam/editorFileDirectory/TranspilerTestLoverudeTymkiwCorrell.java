class Foo{ 

    protected int f = 10;

    public void action(){
    }
}


class Bar extends Foo{ }


class Main extends Foo{ 

    protected int x = 0;
    protected int y = 1;
    protected Foo foo = this;

    public int returnNumber(int x, int y){
        while(x == 0){
            x = 1;
            break;
        }
        if(x < 10){
            x = this.x;
            y = super.f;
        }
        return x + y;
    }

    public String returnString(){
        var s = "Hello";
        var num = returnNumber(2, 3);
        if(s instanceof String){
            s = "Greetings.";
        }
        return s;
    }

    public static void main(String[] args){
        var i = 0;
        for(i = 0; i < 2; i++){
            var j = 0;
            System.out.println("");
            System.out.println("Going to sleep");
            for(j = 0; j < 5; j++){
                System.out.println("Zzz...");
            }
            System.out.println("Waking up!");
            var k = 0;
            while(k < 5){
                System.out.println("Going to class");
                k++;
            }
        }
    }
}


