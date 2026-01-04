# 🌱 智慧温室大棚管理系统 - 后端服务

基于 Spring Boot 3 + Spring Cloud 的智慧农业物联网平台，集成 AI 精准施肥决策。

## 📦 项目结构

```
smart-greenhouse-backend/
├── gateway-service/     # API 网关 (8080)
├── auth-service/        # 认证服务 (8081)
├── device-service/      # 设备管理 (8082)
├── data-service/        # 数据服务 (8083)
├── ai-decision-service/ # AI 决策服务 (8084)
├── vision-service/      # 视觉识别服务 (8085)
└── common/              # 公共模块
```

## 🛠️ 技术栈

- Java 21
- Spring Boot 3.2.4
- Spring Cloud 2023.0.1
- PostgreSQL
- InfluxDB (时序数据)
- DeepSeek API (AI 模型)

## 🚀 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.8+
- PostgreSQL 15+
- InfluxDB 2.x (可选)

### 2. 配置数据库

```sql
-- 创建数据库
CREATE DATABASE greenhouse;

-- 创建土壤数据表
CREATE TABLE fertilizer_history (
    id BIGSERIAL PRIMARY KEY,
    week INTEGER NOT NULL,
    n_soil DECIMAL(10,2),
    p_soil DECIMAL(10,2),
    k_soil DECIMAL(10,2),
    ph DECIMAL(4,2),
    ec DECIMAL(4,2),
    temp DECIMAL(4,2),
    env_status VARCHAR(50),
    advice_json JSONB,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. 配置环境变量

复制配置模板并填入真实值：

```bash
# 数据库
export DB_URL=jdbc:postgresql://localhost:5432/greenhouse
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

# DeepSeek API
export DEEPSEEK_API_KEY=your_api_key

# 讯飞语音
export XUNFEI_APPID=your_appid
export XUNFEI_API_KEY=your_api_key
export XUNFEI_API_SECRET=your_api_secret
```

### 4. 启动服务

```bash
# 编译
mvn clean package -DskipTests

# 启动各服务
java -jar data-service/target/data-service-1.0.0.jar
java -jar ai-decision-service/target/ai-decision-service-1.0.0.jar
```

## 📡 主要 API

### 土壤数据

| 接口 | 方法 | 说明 |
|------|------|------|
| `/data/soil` | GET | 获取最新土壤数据 |
| `/data/soil/history?range=24h` | GET | 获取历史数据 |

### AI 施肥分析

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/fertilizer/analyze` | POST | AI 精准施肥建议 |

请求示例：
```json
{
  "week": 5,
  "N_soil": 120.5,
  "P_soil": 35.2,
  "K_soil": 180.0,
  "ph": 6.5,
  "ec": 2.1,
  "temp": 25.0
}
```

## 📄 License

MIT License
