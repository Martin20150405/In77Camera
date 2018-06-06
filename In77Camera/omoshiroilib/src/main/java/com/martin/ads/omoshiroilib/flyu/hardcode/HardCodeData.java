package com.martin.ads.omoshiroilib.flyu.hardcode;

import java.util.ArrayList;
import java.util.List;

public class HardCodeData {
    public static final String IMAGE_PATH="image_path";

    public static class EffectItem {
        public String name;
        public String zipFilePath;
        public int type;
        public String unzipPath;
        public String description;

        public EffectItem(String name, int type, String unzipPath, String description) {
            this.name = name;
            this.zipFilePath = "faceu_effects/res/"+name+".zip";
            this.type = type;
            this.unzipPath = unzipPath;
            this.description = description;
        }

        public String getThumbFilePath(){
            return "faceu_effects/thumbs/"+name+".png";
        }
    }
    public static List<EffectItem> itemList;
    public static void initHardCodeData(){
        itemList=new ArrayList<>();
        itemList.add(new EffectItem("none", -1, "none","passthrough"));
        itemList.add(new EffectItem("10012_2", 0, "10012_2","rainbow"));
        itemList.add(new EffectItem("50109_2", 1, "50109_2","weisuo / xieyan"));
        itemList.add(new EffectItem("50291_3", 3, "50291_3","fatface"));
        itemList.add(new EffectItem("20088_1_b", 3, "20088_1_b","animal_catfoot_b"));
        itemList.add(new EffectItem("900029_5", 3, "900029_5","smallmouth"));
        itemList.add(new EffectItem("50216_1", 1, "50216_1","zhibo"));
        itemList.add(new EffectItem("170009_2", 2, "170009_2","slim face"));
        itemList.add(new EffectItem("170010_1", 2, "170010_1","mirrorface"));
        itemList.add(new EffectItem("10007_1_sb", 0, "10007_1_sb","shabi"));
        itemList.add(new EffectItem("900317_1_tiger", 3, "900317_1_tiger","tiger"));
    }
}
