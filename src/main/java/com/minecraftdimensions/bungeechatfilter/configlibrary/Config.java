package com.minecraftdimensions.bungeechatfilter.configlibrary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Config {

    private FileConfiguration fconfig;
    private String path;


    public Config( String path ) {
        this.path = System.getProperty( "user.dir" ) + path;

        createFile();
        fconfig = YamlConfiguration.loadConfiguration( new File( System.getProperty( "user.dir" ) + path ) );
    }

    public void load() {
        createFile();
        try {
            fconfig.load( path );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void save() {
        createFile();
        try {
            fconfig.save( path );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void createFile() {
        File file = new File( path );
        if ( !file.exists() ) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch ( Exception e ) {
                //dobug.log(e,true);
                e.printStackTrace();
            }
        }
    }
    
    public Object get( String key ) {

        if ( fconfig.contains( key ) ) {
            return fconfig.get( key );
        } else{
            return null;
        }
    }

    public String getString( String key, String def ) {

        if ( fconfig.contains( key ) ) {
            return fconfig.getString( key );
        } else {
            fconfig.set( key, def );
            try {
                fconfig.save( path );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            return def;
        }
    }

    public String getString( String key ) {

        if ( fconfig.contains( key ) ) {
            return fconfig.getString( key );
        } else{
            return null;
        }
    }

    public int getInt( String key, int def ) {

        if ( fconfig.contains( key ) ) {
            return fconfig.getInt( key );
        } else {
            fconfig.set( key, def );
            try {
                fconfig.save( path );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            return def;
        }

    }
    public int getInt( String key ) {

        if ( fconfig.contains( key ) ) {
            return fconfig.getInt( key );
        } else {
            return 0;
        }

    }


    public boolean getBoolean( String key, boolean def ) {

        if ( fconfig.contains( key ) ) {
            return fconfig.getBoolean( key );
        } else {
            fconfig.set( key, def );
            try {
                fconfig.save( path );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            return def;
        }

    }

    public List<String> getListString( String key, List<String> def ) {

        if ( fconfig.contains( key ) ) {
            return fconfig.getStringList( key );
        } else {
            fconfig.set( key, def );
            try {
                fconfig.save( path );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            return def;
        }

    }

    public List<String> getListString( String key ) {

        if ( fconfig.contains( key ) ) {
            return fconfig.getStringList( key );
        } else {

            return null;
        }

    }

    public double getDouble( String key, double def ) {
        if ( fconfig.contains( key ) ) {
            return fconfig.getDouble( key );
        } else {
            fconfig.set( key, def );
            try {
                fconfig.save( path );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            return def;
        }
    }

    public void setString( String key, String val ) {
        fconfig.set( key, val );
        try {
            fconfig.save( path );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void setInt( String key, int val ) {
        fconfig.set( key, val );
        try {
            fconfig.save( path );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void setBool( String key, boolean val ) {
        fconfig.set( key, val );
        try {
            fconfig.save( path );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void setListString( String key, List<String> val ) {
        fconfig.set( key, val );
        try {
            fconfig.save( path );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public List<String> getSubNodes( String node ) {
        List<String> ret = new ArrayList<String>();
        try {
            for ( Object o : fconfig.getConfigurationSection( node ).getKeys( false ) ) {
                ret.add( ( String ) o );
            }
        } catch ( Exception e ) {
        }
        return ret;
    }


}
