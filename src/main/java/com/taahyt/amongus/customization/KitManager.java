package com.taahyt.amongus.customization;

import com.taahyt.amongus.customization.kits.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KitManager
{

    private List<Kit> kits = new ArrayList<>();

    public KitManager()
    {
        kits.add(new BlueKit());
        kits.add(new GrayKit());
        kits.add(new RedKit());
        kits.add(new TurqoiseKit());
        kits.add(new YellowKit());
    }

    public Kit getRandomKit()
    {
        Kit kit = kits.get(ThreadLocalRandom.current().nextInt(kits.size()));
        kits.remove(kit);
        return kit;
    }

}
