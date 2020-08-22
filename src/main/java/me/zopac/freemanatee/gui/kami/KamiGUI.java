package me.zopac.freemanatee.gui.kami;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.awt.Font;

import javax.annotation.Nonnull;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.zopac.freemanatee.KamiMod;
import me.zopac.freemanatee.command.Command;
import me.zopac.freemanatee.gui.font.CFontRenderer;
import me.zopac.freemanatee.gui.rgui.GUI;
import me.zopac.freemanatee.gui.rgui.component.container.use.Frame;
import me.zopac.freemanatee.gui.rgui.component.container.use.Scrollpane;
import me.zopac.freemanatee.gui.rgui.component.listen.MouseListener;
import me.zopac.freemanatee.gui.rgui.component.listen.TickListener;
import me.zopac.freemanatee.gui.rgui.component.use.CheckButton;
import me.zopac.freemanatee.gui.rgui.component.use.Label;
import me.zopac.freemanatee.gui.rgui.render.theme.Theme;
import me.zopac.freemanatee.gui.rgui.util.ContainerHelper;
import me.zopac.freemanatee.gui.rgui.util.Docking;
import me.zopac.freemanatee.gui.kami.component.ActiveModules;
import me.zopac.freemanatee.gui.kami.component.Radar;
import me.zopac.freemanatee.gui.kami.component.SettingsPanel;
import me.zopac.freemanatee.gui.kami.theme.kami.KamiTheme;
import me.zopac.freemanatee.module.Module;
import me.zopac.freemanatee.module.ModuleManager;
import me.zopac.freemanatee.util.ColourHolder;
import me.zopac.freemanatee.util.LagCompensator;
import me.zopac.freemanatee.util.ModuleMan;
import me.zopac.freemanatee.util.OnlineFriends;
import me.zopac.freemanatee.util.Pair;
import me.zopac.freemanatee.util.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.text.TextFormatting;

import static me.zopac.freemanatee.util.InfoCalculator.cardinalToAxis;

public class KamiGUI extends GUI {

    public ModuleMan manager = new ModuleMan();
    public static final RootFontRenderer fontRenderer = new RootFontRenderer(1);
    public Theme theme;
    public static CFontRenderer cFontRenderer;

    public static ColourHolder primaryColour = new ColourHolder(29, 29, 29);

    public KamiGUI() {
        super(new KamiTheme());
        theme = getTheme();
    }

    @Override
    public void drawGUI() {
        super.drawGUI();
    }

    @Override
    public void initializeGUI() {
        HashMap<Module.Category, Pair<Scrollpane, SettingsPanel>> categoryScrollpaneHashMap = new HashMap<>();
        for (Module module : ModuleManager.getModules()) {
            if (module.getCategory().isHidden()) continue;
            Module.Category moduleCategory = module.getCategory();
            if (!categoryScrollpaneHashMap.containsKey(moduleCategory)) {
                Stretcherlayout stretcherlayout = new Stretcherlayout(1);
                stretcherlayout.setComponentOffsetWidth(0);
                Scrollpane scrollpane = new Scrollpane(getTheme(), stretcherlayout, 300, 260);
                scrollpane.setMaximumHeight(180);
                categoryScrollpaneHashMap.put(moduleCategory, new Pair<>(scrollpane, new SettingsPanel(getTheme(), null)));
            }

            Pair<Scrollpane, SettingsPanel> pair = categoryScrollpaneHashMap.get(moduleCategory);
            Scrollpane scrollpane = pair.getKey();
            CheckButton checkButton = new CheckButton(module.getName());
            checkButton.setToggled(module.isEnabled());

            checkButton.addTickListener(() -> { // dear god
                checkButton.setToggled(module.isEnabled());
                checkButton.setName(module.getName());
            });

            checkButton.addMouseListener(new MouseListener() {
                @Override
                public void onMouseDown(MouseButtonEvent event) {
                    if (event.getButton() == 1) { // Right click
                        pair.getValue().setModule(module);
                        pair.getValue().setX(event.getX() + checkButton.getX());
                        pair.getValue().setY(event.getY() + checkButton.getY());
                    }
                }

                @Override
                public void onMouseRelease(MouseButtonEvent event) {

                }

                @Override
                public void onMouseDrag(MouseButtonEvent event) {

                }

                @Override
                public void onMouseMove(MouseMoveEvent event) {

                }

                @Override
                public void onScroll(MouseScrollEvent event) {

                }
            });
            checkButton.addPoof(new CheckButton.CheckButtonPoof<CheckButton, CheckButton.CheckButtonPoof.CheckButtonPoofInfo>() {
                @Override
                public void execute(CheckButton component, CheckButtonPoofInfo info) {
                    if (info.getAction().equals(CheckButton.CheckButtonPoof.CheckButtonPoofInfo.CheckButtonPoofInfoAction.TOGGLE)) {
                        module.setEnabled(checkButton.isToggled());
                    }
                }
            });
            scrollpane.addChild(checkButton);
        }

        int x = 10;
        int y = 10;
        int nexty = y;
        for (Map.Entry<Module.Category, Pair<Scrollpane, SettingsPanel>> entry : categoryScrollpaneHashMap.entrySet()) {
            Stretcherlayout stretcherlayout = new Stretcherlayout(1);
            stretcherlayout.COMPONENT_OFFSET_Y = 1;
            Frame frame = new Frame(getTheme(), stretcherlayout, entry.getKey().getName());
            Scrollpane scrollpane = entry.getValue().getKey();
            frame.addChild(scrollpane);
            frame.addChild(entry.getValue().getValue());
            scrollpane.setOriginOffsetY(0);
            scrollpane.setOriginOffsetX(0);
            frame.setCloseable(false);

            frame.setX(x);
            frame.setY(y);

            addChild(frame);

            nexty = Math.max(y + frame.getHeight() + 50, nexty);
            x += frame.getWidth() + 10;
            if (x > Wrapper.getMinecraft().displayWidth / 1.2f) {
                y = nexty;
                nexty = y;
            }
        }

        this.addMouseListener(new MouseListener() {
            private boolean isBetween(int min, int val, int max) {
                return !(val > max || val < min);
            }

            @Override
            public void onMouseDown(MouseButtonEvent event) {
                List<SettingsPanel> panels = ContainerHelper.getAllChildren(SettingsPanel.class, KamiGUI.this);
                for (SettingsPanel settingsPanel : panels) {
                    if (!settingsPanel.isVisible()) continue;
                    int[] real = GUI.calculateRealPosition(settingsPanel);
                    int pX = event.getX() - real[0];
                    int pY = event.getY() - real[1];
                    if (!isBetween(0, pX, settingsPanel.getWidth()) || !isBetween(0, pY, settingsPanel.getHeight()))
                        settingsPanel.setVisible(false);
                }
            }

            @Override
            public void onMouseRelease(MouseButtonEvent event) {

            }

            @Override
            public void onMouseDrag(MouseButtonEvent event) {

            }

            @Override
            public void onMouseMove(MouseMoveEvent event) {

            }

            @Override
            public void onScroll(MouseScrollEvent event) {

            }
        });

        ArrayList<Frame> frames = new ArrayList<>();

        Frame frame = new Frame(getTheme(), new Stretcherlayout(1), "Active Modules");
        frame.setCloseable(false);
        frame.addChild(new ActiveModules());
        frame.setPinneable(true);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Welcomer");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label welcomer = new Label("");
        welcomer.setShadow(true);
        welcomer.addTickListener(() -> {
            welcomer.setText("");
            welcomer.addLine("\u00A73 Welcome " + "\u00A75" + Wrapper.getPlayer().getDisplayNameString());
        });
        frame.addChild(welcomer);
        welcomer.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Info");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label information = new Label("");
        information.setShadow(true);
        information.addTickListener(() -> {
            information.setText("");
            information.addLine("\u00A7r\u00A73" + Math.round(LagCompensator.INSTANCE.getTickRate()) + Command.SECTIONSIGN() + "3 tps");
            information.addLine("\u00A7r\u00A73" + Wrapper.getMinecraft().debugFPS + Command.SECTIONSIGN() + "3 fps");

        });
        frame.addChild(information);
        information.setFontRenderer(fontRenderer);
        frames.add(frame);

        frames.add(frame);
        frame = new Frame(getTheme(), new Stretcherlayout(1), "Hole");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label hole = new Label("");
        hole.setShadow(true);
        hole.addTickListener(() -> {
            hole.setText("");
            hole.addLine(" " + manager.getHoleType());
        });
        frame.addChild(hole);
        hole.setFontRenderer(fontRenderer);
        frames.add(frame);

        frames.add(frame);
        frame = new Frame(getTheme(), new Stretcherlayout(1), "PVP Info");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label goodsLabel = new Label("");
        goodsLabel.setShadow(true);
        goodsLabel.addTickListener(() -> {
            goodsLabel.setText("");
            goodsLabel.addLine(" " + manager.isAura());
//            goodsLabel.addLine(" " + manager.isAura2());
            goodsLabel.addLine(" " + manager.isTrap());
            goodsLabel.addLine(" " + manager.isFill());
            goodsLabel.addLine(" " + manager.isSelfTrap());
            goodsLabel.addLine(" " + manager.isSurround());
        });
        frame.addChild(goodsLabel);
        goodsLabel.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Friend List");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label friendLabel = new Label("");
        friendLabel.setShadow(true);
        friendLabel.addTickListener(() -> {
            friendLabel.setText("");
            if (OnlineFriends.getFriends().isEmpty()) {
                friendLabel.addLine("");
            } else {
                friendLabel.addLine("\u00A7l\u00A79 Friends");
                for (Entity e : OnlineFriends.getFriends()) {
                    friendLabel.addLine("\u00A76 " + e.getName());
                }
            }
        });
        frame.addChild(friendLabel);
        friendLabel.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Totems");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label totem = new Label("");
        totem.setShadow(true);
        totem.addTickListener(() -> {
            totem.setText("");
            int crystalCount = 0;
            for (int i = 0; i < 45; i++) {
                ItemStack itemStack = Wrapper.getMinecraft().player.inventory.getStackInSlot(i);
                if (itemStack.getItem() == Items.END_CRYSTAL) {
                    crystalCount += itemStack.stackSize;
                }
            }
            totem.addText((ChatFormatting.BOLD.DARK_BLUE) + "Totems: " + (ChatFormatting.AQUA) + String.valueOf(crystalCount));
        });
        frame.addChild(totem);
        totem.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Crystals");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label crystals = new Label("");
        crystals.setShadow(true);
        crystals.addTickListener(() -> {
            crystals.setText("");
            int crystalCount = 0;
            for (int i = 0; i < 45; i++) {
                ItemStack itemStack = Wrapper.getMinecraft().player.inventory.getStackInSlot(i);
                if (itemStack.getItem() == Items.END_CRYSTAL) {
                    crystalCount += itemStack.stackSize;
                }
            }
            crystals.addText((ChatFormatting.BOLD.LIGHT_PURPLE) + "Crystals: " + (ChatFormatting.AQUA) + String.valueOf(crystalCount));
        });
        frame.addChild(crystals);
        crystals.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Gapples");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label gapples = new Label("");
        gapples.setShadow(true);
        gapples.addTickListener(() -> {
            gapples.setText("");
            int gappleCount = 0;
            for (int i = 0; i < 45; i++) {
                ItemStack itemStack = Wrapper.getMinecraft().player.inventory.getStackInSlot(i);
                if (itemStack.getItem() == Items.GOLDEN_APPLE && itemStack.getItemDamage() == 1) {
                    gappleCount += itemStack.stackSize;
                }
            }
            gapples.addText((ChatFormatting.BOLD.YELLOW) + "Gapples: " + (ChatFormatting.AQUA) + String.valueOf(gappleCount));
        });
        frame.addChild(gapples);
        gapples.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "EXP");
        frame.setCloseable(false);
        frame.setPinneable(true);
        Label xp = new Label("");
        xp.setShadow(true);
        xp.addTickListener(() -> {
            xp.setText("");
            int xpCount = 0;
            for (int i = 0; i < 45; i++) {
                ItemStack itemStack = Wrapper.getMinecraft().player.inventory.getStackInSlot(i);
                if (itemStack.getItem() == Items.EXPERIENCE_BOTTLE) {
                    xpCount += itemStack.stackSize;
                }
            }
            xp.addText((ChatFormatting.BOLD.DARK_GREEN) + "EXP: " + (ChatFormatting.AQUA) + String.valueOf(xpCount));
        });
        frame.addChild(xp);
        xp.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Text Radar");
        Label list = new Label("");
        DecimalFormat dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.HALF_UP);
        StringBuilder healthSB = new StringBuilder();
        list.addTickListener(() -> {
            if (!list.isVisible()) return;
            list.setText("");

            Minecraft mc = Wrapper.getMinecraft();

            if (mc.player == null) return;
            List<EntityPlayer> entityList = mc.world.playerEntities;

            Map<String, Integer> players = new HashMap<>();
            for (Entity e : entityList) {
                if (e.getName().equals(mc.player.getName())) continue;
                String posString = (e.posY > mc.player.posY ? ChatFormatting.DARK_GREEN + "+" : (e.posY == mc.player.posY ? " " : ChatFormatting.DARK_RED + "-"));
                float hpRaw = ((EntityLivingBase) e).getHealth() + ((EntityLivingBase) e).getAbsorptionAmount();
                String hp = dfHealth.format(hpRaw);
                healthSB.append(Command.SECTIONSIGN());
                if (hpRaw >= 20) {
                    healthSB.append("a");
                } else if (hpRaw >= 10) {
                    healthSB.append("e");
                } else if (hpRaw >= 5) {
                    healthSB.append("6");
                } else {
                    healthSB.append("c");
                }
                healthSB.append(hp);
                players.put(ChatFormatting.GRAY + posString + " " + healthSB.toString() + " " + ChatFormatting.GRAY + e.getName(), (int) mc.player.getDistance(e));
                healthSB.setLength(0);
            }

            if (players.isEmpty()) {
                list.setText("");
                return;
            }

            players = sortByValue(players);

            for (Map.Entry<String, Integer> player : players.entrySet()) {
                list.addLine(Command.SECTIONSIGN() + "7" + player.getKey() + " " + Command.SECTIONSIGN() + "8" + player.getValue());
            }
        });
        frame.setCloseable(false);
        frame.setPinneable(true);
        frame.setMinimumWidth(75);
        list.setShadow(true);
        frame.addChild(list);
        list.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Entities");
        Label entityLabel = new Label("");
        frame.setCloseable(false);
        entityLabel.addTickListener(new TickListener() {
            Minecraft mc = Wrapper.getMinecraft();

            @Override
            public void onTick() {
                if (mc.player == null || !entityLabel.isVisible()) return;

                final List<Entity> entityList = new ArrayList<>(mc.world.loadedEntityList);
                if (entityList.size() <= 1) {
                    entityLabel.setText("");
                    return;
                }
                final Map<String, Integer> entityCounts = entityList.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> !(e instanceof EntityPlayer))
                        .collect(Collectors.groupingBy(KamiGUI::getEntityName,
                                Collectors.reducing(0, ent -> {
                                    if (ent instanceof EntityItem)
                                        return ((EntityItem) ent).getItem().getCount();
                                    return 1;
                                }, Integer::sum)
                        ));

                entityLabel.setText("");
                entityCounts.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(entry -> TextFormatting.GRAY + entry.getKey() + " " + TextFormatting.DARK_GRAY + "x" + entry.getValue())
                        .forEach(entityLabel::addLine);

                entityLabel.getParent().setHeight(entityLabel.getLines().length * (entityLabel.getTheme().getFontRenderer().getFontHeight()+1) + 3);
            }
        });
        frame.addChild(entityLabel);
        frame.setPinneable(true);
        entityLabel.setShadow(true);
        entityLabel.setFontRenderer(fontRenderer);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Coordinates");
        Label coordsLabel = new Label("");
        frame.setCloseable(false);
        frame.setPinneable(true);
        coordsLabel.addTickListener(new TickListener() {
            Minecraft mc = Minecraft.getMinecraft();


            @Override
            public void onTick() {
                boolean inHell = (mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell"));

                int posX = (int) mc.player.posX;
                int posY = (int) mc.player.posY;
                int posZ = (int) mc.player.posZ;

                float f = !inHell ? 0.125f : 8;
                int hposX = (int) (mc.player.posX * f);
                int hposZ = (int) (mc.player.posZ * f);

                coordsLabel.setText(String.format(" %sf%,d%s7, %sf%,d%s7, %sf%,d %s7(%sf%,d%s7, %sf%,d%s7, %sf%,d%s7)",
                        Command.SECTIONSIGN(),
                        posX,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        posY,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        posZ,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        hposX,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        posY,
                        Command.SECTIONSIGN(),
                        Command.SECTIONSIGN(),
                        hposZ,
                        Command.SECTIONSIGN()
                ));
            }
        });
        frame.addChild(coordsLabel);
        coordsLabel.setFontRenderer(fontRenderer);
        coordsLabel.setShadow(true);
        frame.setHeight(20);
        frames.add(frame);

        frame = new Frame(getTheme(), new Stretcherlayout(1), "Radar");
        frame.setCloseable(false);
        frame.setMinimizeable(true);
        frame.setPinneable(true);
        frame.addChild(new Radar());
        frame.setWidth(100);
        frame.setHeight(100);
        frames.add(frame);

        for (Frame frame1 : frames) {
            frame1.setX(x);
            frame1.setY(y);

            nexty = Math.max(y + frame1.getHeight() + 10, nexty);
            x += frame1.getWidth() + 10;
            if (x * DisplayGuiScreen.getScale() > Wrapper.getMinecraft().displayWidth / 1.2f) {
                y = nexty;
                nexty = y;
                x = 10;
            }

            addChild(frame1);
        }
    }

    private static String getEntityName(@Nonnull Entity entity) {
        if (entity instanceof EntityItem) {
            return TextFormatting.DARK_AQUA + ((EntityItem) entity).getItem().getItem().getItemStackDisplayName(((EntityItem) entity).getItem());
        }
        if (entity instanceof EntityWitherSkull) {
            return TextFormatting.DARK_GRAY + "Wither skull";
        }
        if (entity instanceof EntityEnderCrystal) {
            return TextFormatting.LIGHT_PURPLE + "End crystal";
        }
        if (entity instanceof EntityEnderPearl) {
            return "Thrown ender pearl";
        }
        if (entity instanceof EntityMinecart) {
            return "Minecart";
        }
        if (entity instanceof EntityItemFrame) {
            return "Item frame";
        }
        if (entity instanceof EntityEgg) {
            return "Thrown egg";
        }
        if (entity instanceof EntitySnowball) {
            return "Thrown snowball";
        }

        return entity.getName();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, Comparator.comparing(o -> (o.getValue())));

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public void destroyGUI() {
        kill();
    }

    private static final int DOCK_OFFSET = 0;

    public static void dock(Frame component) {
        Docking docking = component.getDocking();
        if (docking.isTop())
            component.setY(DOCK_OFFSET);
        if (docking.isBottom())
            component.setY((Wrapper.getMinecraft().displayHeight / DisplayGuiScreen.getScale()) - component.getHeight() - DOCK_OFFSET);
        if (docking.isLeft())
            component.setX(DOCK_OFFSET);
        if (docking.isRight())
            component.setX((Wrapper.getMinecraft().displayWidth / DisplayGuiScreen.getScale()) - component.getWidth() - DOCK_OFFSET);
        if (docking.isCenterHorizontal())
            component.setX((Wrapper.getMinecraft().displayWidth / (DisplayGuiScreen.getScale() * 2) - component.getWidth() / 2));
        if (docking.isCenterVertical())
            component.setY(Wrapper.getMinecraft().displayHeight / (DisplayGuiScreen.getScale() * 2) - component.getHeight() / 2);

    }

    static {
        KamiGUI.cFontRenderer = new CFontRenderer(new Font("comic sans", 0, 18), true, false);
    }
}