import mysql from 'mysql2/promise';

type JdbcConfig = {
  host: string;
  port: number;
  database: string;
  user: string;
  password: string;
};

function parseJdbcUrl(): JdbcConfig {
  const rawUrl = process.env.DB_URL || 'jdbc:mysql://localhost:3306/kiaev_e2e';
  const match = rawUrl.match(/^jdbc:mysql:\/\/([^:/?]+)(?::(\d+))?\/([^?]+)/);

  if (!match) {
    throw new Error(`Unsupported DB_URL format: ${rawUrl}`);
  }

  return {
    host: match[1],
    port: Number(match[2] || 3306),
    database: match[3],
    user: process.env.DB_USERNAME || 'root',
    password: process.env.DB_PASSWORD || '',
  };
}

export async function createDbConnection() {
  const config = parseJdbcUrl();
  const db = await mysql.createConnection({
    host: config.host,
    port: config.port,
    user: config.user,
    password: config.password,
    multipleStatements: true,
  });
  await db.query(`CREATE DATABASE IF NOT EXISTS \`${config.database}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci`);
  await db.changeUser({ database: config.database });
  return db;
}
