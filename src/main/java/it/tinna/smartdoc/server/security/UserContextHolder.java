package it.tinna.smartdoc.server.security;

public class UserContextHolder
{

    private static final ThreadLocal<Integer> tipoAccountHolder = new ThreadLocal<>();

    public static void setTipoAccount(Integer tipoAccount)
    {
        tipoAccountHolder.set(tipoAccount);
    }

    public static Integer getTipoAccount()
    {
        return tipoAccountHolder.get();
    }

    public static void clear()
    {
        tipoAccountHolder.remove();
    }

}
