package io.github.ajcode404;

import java.util.ArrayList;
import java.util.List;
public class CredExample {

    List<List<Integer>> data = new ArrayList<>();

    abstract class Data {
        private Integer x;
        private Integer y;
        private Integer value;

        public Data(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setValue(int value) {
            data.get(x).set(y, value);
        }

    }

    class Number extends Data {

        public Number(int x, int y) {
            super(x, y);
        }

        void update(int value) {
            setValue(value);
        }
    }

    class SumFunction extends Data {

        private String op = "SUM";
        private String[] args;

        public SumFunction(String[] args, int x, int y) {
            super(x, y);
            this.args = args;
        }

        void update() {
            setValue();
        }

        private void execute() {
            // execute args

        }
    }

}


