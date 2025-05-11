package yuku.dsupport.partyUI;

import java.util.HashSet;
import java.util.Set;

public class PartyTracker {
    public static String currentOwner = null;
    public static final Set<String> currentMembers = new HashSet<>();

    public static void resetParty() {
        currentOwner = null;
        currentMembers.clear();
        System.out.println("❌ パーティー状態リセット");
    }

    public static void join(String playerName) {
        currentMembers.add(playerName);
        System.out.println("✅ 参加: " + playerName);
    }

    public static void leave(String playerName) {
        currentMembers.remove(playerName);
        System.out.println("🚪 離脱: " + playerName);
    }

    public static void setLeader(String playerName) {
        currentOwner = playerName;
        currentMembers.add(playerName); // 念のため追加
        System.out.println("👑 新リーダー: " + playerName);
    }
    /*
    public static void init() {
        ClientReceiveMessageEvents.CHAT((message, signed, sender, params) -> {
            String text = message.

            // 所有者からスタート
            if (text.startsWith("パーティーの所有者:")) {
                partyMembers.clear();
                isParsingParty = true;

                String owner = text.replace("パーティーの所有者:", "").trim();
                partyMembers.add(owner);
                return;
            }

            if (isParsingParty) {
                // メンバー名が続く場合（空行や記号などがない場合）
                if (!text.isEmpty() && text.length() < 20 && !text.contains(" ")) {
                    partyMembers.add(text.trim());
                } else {
                    // それ以外の行が来たらパース終了
                    isParsingParty = false;
                }
            }
        });
    }*/
}