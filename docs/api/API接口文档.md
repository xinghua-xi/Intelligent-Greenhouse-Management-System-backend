# 智慧温室后端 API 接口文档

> 网关地址：`http://localhost:8080`  
> 更新时间：2025-12-23

---

## 1. 认证服务 (Auth) - 端口 8081

### 1.1 用户登录
- **路径**: `POST /auth/login`
- **请求**:
```json
{
  "username": "admin",
  "password": "123456"
}
```
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": "uuid",
      "username": "admin",
      "role": "EXPERT",
      "defaultMode": "EXPERT",
      "createdAt": "2025-12-23T10:00:00"
    }
  }
}
```

### 1.2 获取用户列表
- **路径**: `GET /auth/users`
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": [
    {"id": "uuid", "username": "admin", "role": "EXPERT", "defaultMode": "EXPERT", "createdAt": "2025-12-23T10:00:00"}
  ]
}
```

### 1.3 获取单个用户
- **路径**: `GET /auth/users/{id}`
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": {"id": "uuid", "username": "admin", "role": "EXPERT", "defaultMode": "EXPERT"}
}
```

### 1.4 创建用户
- **路径**: `POST /auth/users`
- **请求**:
```json
{
  "username": "user1",
  "password": "123456",
  "role": "STANDARD",
  "defaultMode": "STANDARD"
}
```
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": {"id": "uuid", "username": "user1", "role": "STANDARD"}
}
```

### 1.5 更新用户
- **路径**: `PUT /auth/users/{id}`
- **请求**:
```json
{
  "username": "newname",
  "password": "newpassword",
  "role": "EXPERT"
}
```

### 1.6 删除用户
- **路径**: `DELETE /auth/users/{id}`
- **响应**:
```json
{"code": 200, "msg": "Success", "data": "删除成功"}
```

---

## 2. AI 服务 - 端口 8084

### 2.1 智慧问答
- **路径**: `POST /ai/chat`
- **请求**:
```json
{
  "prompt": "番茄叶子发黄怎么办？",
  "history": [
    {"role": "user", "content": "之前的问题"},
    {"role": "assistant", "content": "之前的回答"}
  ],
  "greenhouseId": "gh_001"
}
```
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": {
    "success": true,
    "text": "番茄叶子发黄可能是缺氮或浇水过多，建议检查土壤湿度和施肥情况",
    "model": "deepseek-chat",
    "timestamp": 1703289600000
  }
}
```

### 2.2 语音转文字
- **路径**: `POST /ai/speech-to-text`
- **请求**:
```json
{
  "audio": "Base64编码的音频数据",
  "format": "m4a"
}
```
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": {"text": "识别出的文字内容"}
}
```

### 2.3 AI 托管建议
- **路径**: `GET /ai/decision/recommend`
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": {
    "action": "IRRIGATION",
    "reason": "检测到土壤含水量(28%)低于设定阈值(30%)",
    "confidence": 0.92
  }
}
```

### 2.4 智能排产任务
- **路径**: `GET /ai/schedule/tasks`
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": [
    {"id": "task_01", "type": "irrigation", "status": "pending", "aiConfidence": 0.88},
    {"id": "task_02", "type": "fertilizer", "status": "pending", "aiConfidence": 0.75}
  ]
}
```

### 2.5 知识库文章列表
- **路径**: `GET /ai/articles`
- **参数**:
  - `category` (可选): PEST, DISEASE, PLANTING, MANAGEMENT
  - `cropType` (可选): 番茄, 黄瓜, 草莓 等
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": [
    {
      "id": "art_001",
      "title": "番茄白粉病防治指南",
      "content": "白粉病是番茄常见真菌病害...",
      "category": "DISEASE",
      "cropType": "番茄",
      "author": "农技专家",
      "viewCount": 156,
      "coverImage": "https://...",
      "tags": "白粉病,番茄,防治",
      "createdAt": "2025-12-23T10:00:00",
      "updatedAt": "2025-12-23T10:00:00"
    }
  ]
}
```

### 2.6 搜索文章
- **路径**: `GET /ai/articles/search`
- **参数**: `keyword` (必填)
- **示例**: `GET /ai/articles/search?keyword=白粉病`

### 2.7 热门文章
- **路径**: `GET /ai/articles/hot`
- **说明**: 返回浏览量前10的文章

### 2.8 文章详情
- **路径**: `GET /ai/articles/{id}`
- **说明**: 访问后自动增加浏览量

### 2.9 创建文章
- **路径**: `POST /ai/articles`
- **请求**:
```json
{
  "title": "番茄白粉病防治指南",
  "content": "白粉病是番茄常见病害...",
  "category": "DISEASE",
  "cropType": "番茄",
  "author": "农技专家",
  "tags": "白粉病,番茄,防治",
  "coverImage": "https://..."
}
```

### 2.10 更新文章
- **路径**: `PUT /ai/articles/{id}`

### 2.11 删除文章
- **路径**: `DELETE /ai/articles/{id}`

---

## 3. 设备服务 (Device) - 端口 8082

### 3.1 获取大棚列表
- **路径**: `GET /devices/greenhouses`
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": [
    {
      "id": "gh_001",
      "name": "1号温室",
      "crop": "番茄",
      "status": "NORMAL",
      "healthScore": 85,
      "location": {"x": 120.5, "y": 30.2},
      "createdAt": "2025-12-23T10:00:00"
    }
  ]
}
```

### 3.2 获取大棚详情
- **路径**: `GET /devices/greenhouses/{id}/detail`
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": {
    "info": {"id": "gh_001", "name": "1号温室", "crop": "番茄"},
    "zones": [
      {
        "zone": {"id": "zone_001", "name": "A区"},
        "devices": [
          {"id": "actuator_001", "name": "1号风机", "type": "FAN", "currentValue": "ON"}
        ]
      }
    ]
  }
}
```

### 3.3 发送设备控制指令
- **路径**: `POST /devices/{deviceId}/control`
- **请求**:
```json
{
  "mode": "MANUAL",
  "action": "IRRIGATION",
  "duration": 300
}
```
- **响应**:
```json
{"code": 200, "msg": "Success", "data": "指令已发送"}
```

### 3.4 获取节点状态
- **路径**: `GET /devices/nodes`
- **参数**: `greenhouseId` (可选)
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": [
    {
      "id": "node_001",
      "name": "温湿度传感器1",
      "greenhouseId": "gh_001",
      "nodeType": "SENSOR",
      "signalStrength": 92,
      "battery": 85,
      "status": "ONLINE",
      "lastHeartbeat": "2025-12-23T16:30:00",
      "createdAt": "2025-12-23T10:00:00"
    }
  ]
}
```

**nodeType 枚举值**: SENSOR, GATEWAY, RELAY  
**status 枚举值**: ONLINE, OFFLINE, WARNING

---

## 4. 数据服务 (Data) - 端口 8083

### 4.1 上传传感器数据
- **路径**: `POST /data/upload`
- **请求**:
```json
{
  "greenhouseId": "gh_001",
  "temperature": 25.5,
  "humidity": 60.0
}
```
- **响应**:
```json
{"code": 200, "msg": "Success", "data": "Data saved successfully"}
```

### 4.2 获取环境数据（时序）
- **路径**: `GET /data/environment`
- **参数**:
  - `greenhouseId` (可选): 大棚ID
  - `range` (可选): 时间范围，可选值 `1h`, `24h`(默认), `7d`
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": [
    {"time": "06:00", "temp": 18.5, "humidity": 80, "light": 10, "co2": 400, "voltage": 3.8},
    {"time": "09:00", "temp": 22.3, "humidity": 75, "light": 45, "co2": 420, "voltage": 3.7},
    {"time": "12:00", "temp": 28.1, "humidity": 60, "light": 90, "co2": 380, "voltage": 4.1},
    {"time": "15:00", "temp": 26.0, "humidity": 55, "light": 70, "co2": 390, "voltage": 4.0},
    {"time": "18:00", "temp": 23.2, "humidity": 65, "light": 30, "co2": 410, "voltage": 3.9},
    {"time": "21:00", "temp": 20.0, "humidity": 78, "light": 0, "co2": 450, "voltage": 3.8}
  ]
}
```

**字段说明**:
| 字段 | 类型 | 说明 |
|------|------|------|
| time | string | 时间点 (HH:mm) |
| temp | number | 温度 (°C) |
| humidity | number | 湿度 (%) |
| light | number | 光照强度 (0-100) |
| co2 | number | CO2 浓度 (ppm) |
| voltage | number | 电压 (V) |

---

## 5. 视觉服务 (Vision) - 端口 8085

### 5.1 病虫害识别
- **路径**: `POST /vision/diagnosis`
- **请求**:
```json
{
  "description": "番茄叶片有黄色斑点，叶背有白色粉状物",
  "cropType": "番茄"
}
```
- **响应**:
```json
{
  "code": 200,
  "msg": "Success",
  "data": {
    "condition": "disease",
    "disease": "白粉病",
    "confidence": 0.85,
    "treatment": "建议喷洒多菌灵，加强通风，降低湿度"
  }
}
```

---

## 统一响应格式

```json
{
  "code": 200,      // 状态码：200成功，400参数错误，401未授权，404不存在，500服务器错误
  "msg": "Success", // 提示信息
  "data": {}        // 业务数据
}
```

## 认证方式

除登录接口外，其他接口需在 Header 中携带 Token：
```
Authorization: Bearer <token>
```

## 服务端口汇总

| 服务 | 端口 | 说明 |
|------|------|------|
| gateway-service | 8080 | API 网关（统一入口） |
| auth-service | 8081 | 认证服务 |
| device-service | 8082 | 设备服务 |
| data-service | 8083 | 数据服务 |
| ai-decision-service | 8084 | AI 决策服务 |
| vision-service | 8085 | 视觉服务 |

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token无效 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
