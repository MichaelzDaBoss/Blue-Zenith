package cat.module.modules.misc;

import cat.events.impl.PacketEvent;
import cat.events.impl.UpdateEvent;
import cat.module.Module;
import cat.module.ModuleCategory;
import cat.module.value.types.BooleanValue;
import cat.module.value.types.IntegerValue;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.MillisTimer;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class StaffDetector extends Module {

    public final IntegerValue timeout = new IntegerValue("Delay", 1000, 500, 10000, 10, true, null);

    ArrayList<String> staffUsernames = new ArrayList<>(Arrays.asList("Jinaaan _JustMix Eissaa 1Mhmmd fromthebeginning 1Brhom unhacking AssassinTime PerfectRod_ Ahmmd xImTaiG_ xIBerryPlayz comsterr 1Sweet Ev2n 1F5aMH___3oo 1M7mdz noworneever iMehdi_ xMz7 EyesO_Diamond 1Daykel Aboz3bl qB6o6 506_ fromthestart ZANAD Muntadher Boviix WalriderTime ixBander Thenvra CutieRana iDrakola9 MK_F16 zayedk 1HeyImHasson_ rcski iiRaivy IMMT3H M7mmd Agressives M4rwaan lovelywords 1Narwhql qMabel Sadlly iLuvSG_ Creegam Bastic 1Armia yzed 1M7mmD Fqqris iS3od_ iikimo 420kinaka iiM7mdZ_ Yaazzeed 1LaB Refolt Aymann_ Maarcii Flineer ImAhMeeD Rwxa maqros_hi 1SaQeR keaawe Y2men 1Mex KaaReeeM A7mmd MightyM7MD 1Pepe_ 1FlyingDuck 8mhh Zqvies 0Ivy 69Kaatrin 1RealFadi Mdqz FANOR_SY Wesccar Driction GlND 1ELY_ xDupzHell Fta7 DeeRx A3loosh 1Tz3bo 0hOmar PT7 Y_04 Kuizano sweetyheartt MaRI_32 ImXann MrProfessor_T JustDrink_ 7MZH SalemBayern_ Tostiebramkaas Logrity lacvna iSolom BarryTub xILigutMonster crizen DwLH OneMqnArmy violeeeeet BoMshary Oxenaa 1Kw3zfTea_ S3rvox DangPavel Banderr LipstickSmoker iRxv KingHOYT Jrx7 1Loga_ LEGITT1st 0Da3s 7sawy xiLegitH1ts w7r Klinqc inVertice vdhvm SlayerDarrk _Vxpe AFG_progamer92 InjjectoR Veshan vxom Nizoki6813 TryPop xiDayzer Ba6ee5man qPerfectPvP_ Solivion iAhmedGG Criv_IQ yQuack iMajedi1 Punshmit BlackOurs Ba1z _R3 Watchdog iA11 1ASSASSINATION_ i3li 1KhaleeD 0Aix 1L7NN Dark_shadow04yt DaBabyFan Turnks xLayrix DrMonteeey ToFy_ 1RE3 Nikolas44 1_3bdalH 1_aq R2shed xsatoo wzii obaida123445 ForMercyInsvun SpecialAdel_ vNext_ 0h_Roby yff3 1A7mad1 Malfore_ __Seif Burakula_CB 2526 qlxc Raceth solidperson FexoraNEP Haifa_magic Stranger3reka 7lawaah G3rryx 1MoMYa MX_7mode_yt xMercyBullet HackProtection OP1_ oTMRz OffTnime mokgii Kr2steen Lysreax LX_D Im_A d5qq DelacroiiX yosife_7Y pKaTaNa mahew_x2 yasuisforsale 0PvP_ Cryslinq hsjwCZ QaMutaZ BaSiL_123 Mythiques I5_1 MaybeHeDoes xanaxdoctor IxKimo HerKinq_ TheRampage_king i_Ym5 z7knn 2xr1 wl3d Mondoros Y2serr leafings Destroyerxayu_ o90x _DevilMan Dus1ty oMD_ _sadeq 90fa bota_69 1RodSkiller manuelmaster 9we 0DrRep uh8e I_can_See Ruxq A7md___ SpecialAdam_ IScaryum BinDontCare _ImKH Firas CaughtVaping AuthoritiesVenom 3zam69s ifeelgood91 qRaOuF_ Sp0tzy_ Aboal3z14 AwKTaM Alaam_FG MMZ33333333 Neeres StrengthSimp cxdle abd0_369 Jarxay xBl2ckNight Du7ym Lunching HM___ p89d SlayerWesccar RealWayne BIaler uncolour M7mmd___ AbdullahIq _z2_ Another3DJ INFAMOUSEEE OnlyY2MaaC vMohamed vM6r zxcf_ iiEsaTKing_ MR1_ N6R OMAR_FB Alfredo24151 1Nzar Raresly TheOnlyM7MAD 1Soin 1Alicee kostasidk StrangerM39ob AhmedPROGG _iSkyla iBlaa3 ln5b 1Derex thaiboy_69 Xhappy__ 6_Turtle_9 1l00 vFahad_ iDhoom 7sO".toLowerCase().split(" ")));

    public StaffDetector() {
        super("StaffDetector", "BlocksMC", ModuleCategory.MISC);
    }

    MillisTimer timer = new MillisTimer();

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        if(timer.hasTimeReached(timeout.get())) {
            for(NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap()) {
                if(staffUsernames.contains(info.getGameProfile().getName().toLowerCase())) {
                    NotificationManager.addNoti("STAFF DETECTED!", "Detected {staff}!".replace("{staff}", info.getGameProfile().getName()), NotificationType.WARNING, 1500);
                }
            }
            timer.reset();
        }
    }

}

