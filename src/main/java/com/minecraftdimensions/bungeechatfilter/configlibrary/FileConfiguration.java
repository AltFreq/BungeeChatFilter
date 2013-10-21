package com.minecraftdimensions.bungeechatfilter.configlibrary;

import java.io.*;

public abstract class FileConfiguration extends MemoryConfiguration {
    public FileConfiguration() {
        super();
    }

    public FileConfiguration( Configuration defaults ) {
        super( defaults );
    }

    public void save( File file ) throws IOException {
        file.getParentFile().mkdirs();

        String data = saveToString();

        FileWriter writer = new FileWriter( file );

        try {
            writer.write( data );
        } finally {
            writer.close();
        }
    }

    public void save( String file ) throws IOException {

        save( new File( file ) );
    }

    public abstract String saveToString();

    public void load( File file ) throws FileNotFoundException, IOException, InvalidConfigurationException {
        load( new FileInputStream( file ) );
    }

    public void load( InputStream stream ) throws IOException, InvalidConfigurationException {

        InputStreamReader reader = new InputStreamReader( stream );
        StringBuilder builder = new StringBuilder();
        BufferedReader input = new BufferedReader( reader );


        try {
            String line;

            while ( ( line = input.readLine() ) != null ) {
                builder.append( line );
                builder.append( '\n' );
            }
        } finally {
            input.close();
        }

        loadFromString( builder.toString() );
    }

    public void load( String file ) throws FileNotFoundException, IOException, InvalidConfigurationException {

        load( new File( file ) );
    }

    public abstract void loadFromString( String contents ) throws InvalidConfigurationException;

    protected abstract String buildHeader();

    @Override
    public FileConfigurationOptions options() {
        if ( options == null ) {
            options = new FileConfigurationOptions( this );
        }

        return ( FileConfigurationOptions ) options;
    }
}
