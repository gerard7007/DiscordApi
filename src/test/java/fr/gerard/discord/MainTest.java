package fr.gerard.discord;

import fr.gerard.discord.api.Discord;

public class MainTest {

    public static void main(String[] args) throws Exception {
        try (Discord discord = new Discord.Builder().token("okay").build()) {
            System.out.println(discord.getCookie());
        }
    }
}
