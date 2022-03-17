package io.gottabe.commons.util;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class DatabaseExporterTool {

    private static enum ExportType {
        CREATE,
        CREATE_ONLY,
        UPDATE,
        DROP
    }

    public static void export(ExportType type) throws Exception {

        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(new org.postgresql.Driver(),
                "jdbc:postgresql://localhost:5432/gottabe", "postgres", "postgres");

        Map<String, String> settings = new HashMap<>();
        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL9Dialect");
        settings.put(Environment.PHYSICAL_NAMING_STRATEGY, SpringPhysicalNamingStrategy.class.getName());

        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(settings)
                .applySetting(Environment.DATASOURCE, dataSource).build();

        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        new LocalSessionFactoryBuilder(null, resourceLoader, metadataSources)
                .scanPackages("io.gottabe.commons.entities");
        Metadata metadata = metadataSources.buildMetadata();

        if (type == ExportType.CREATE || type == ExportType.CREATE_ONLY || type == ExportType.DROP) {
            SchemaExport schemaExport = new SchemaExport();
            schemaExport.setFormat(true);
            schemaExport.setDelimiter(";");
            schemaExport.setOutputFile("commons/src/main/resources/db/migration/" + type.name().toLowerCase() + "s.sql");
            if (type == ExportType.CREATE)
                schemaExport.create(EnumSet.of(TargetType.SCRIPT), metadata);
            else if (type == ExportType.CREATE_ONLY)
                schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
            else if (type == ExportType.DROP)
                schemaExport.drop(EnumSet.of(TargetType.SCRIPT), metadata);
        } else if (type == ExportType.UPDATE) {
            SchemaUpdate updates = new SchemaUpdate();
            updates.setFormat(true);
            updates.setDelimiter(";");
            updates.setOutputFile("commons/src/main/resources/db/migration/" + type.name().toLowerCase() + "s.sql");
            updates.execute(EnumSet.of(TargetType.SCRIPT), metadata, serviceRegistry);
        }
    }

    static int calculateChecksum(InputStream is) throws IOException {
        final CRC32 crc32 = new CRC32();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            crc32.update(line.getBytes("UTF-8"));
        }
        return (int) crc32.getValue();
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            if (args.length == 2 && args[0].equals("CHECKSUM"))
                System.out.println(calculateChecksum(DatabaseExporterTool.class.getResourceAsStream(args[1])));
            else
                export(args.length > 0 ? ExportType.valueOf(args[0].toUpperCase()) : ExportType.CREATE);
        }
    }

}
