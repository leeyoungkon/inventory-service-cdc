# inventory-service

Kafka에서 `order.created` 이벤트를 받아 MySQL 재고를 차감하고, 웹 화면과 REST API로 현재 재고를 보여주는 Spring Boot 프로젝트입니다.

## 기능
- Kafka consumer로 주문 이벤트 수신
- `productName`, `quantity` 기준 재고 차감
- MySQL `inventory` 테이블 업데이트
- HTML 화면: `/inventory`
- REST API: `/api/inventory`, `/api/inventory/{productName}`

## 주문 이벤트 예시
```json
{
  "orderNo": "ORD-1001",
  "productName": "Keyboard",
  "quantity": 2
}
```

## 로컬 실행용 설정
기본값:
- MySQL: `jdbc:mysql://localhost:3306/inventorydb`
- Kafka: `localhost:9092`

## Kubernetes 배포 전 준비
이 프로젝트는 다음을 가정합니다.
- Strimzi Kafka가 이미 설치되어 있음
- Kafka bootstrap service: `my-cluster-kafka-bootstrap.kafka:9092`
- MySQL service: `mysql.default.svc.cluster.local:3306`
- MySQL secret: `mysql-secret`, key: `root-password`

## 이미지 빌드
```bash
mvn clean package -DskipTests
docker build -t yourrepo/inventory-service:latest .
```

## Kubernetes 배포
이미지명 수정:
- `k8s/02-inventory-service.yaml`의 `yourrepo/inventory-service:latest`

적용:
```bash
kubectl apply -f k8s/02-inventory-service.yaml
kubectl apply -f k8s/03-inventory-ingress.yaml
```

## 데이터 초기화
`k8s/01-inventory-init-configmap.yaml`는 inventory DB 초기 SQL 예시입니다.
다만 이미 MySQL이 떠 있는 상태라면 자동 실행되지는 않으므로, 아래 둘 중 하나로 사용하세요.

### 방법 1. 수동 실행
```bash
kubectl cp k8s/01-inventory-init-configmap.yaml default/<some-pod>:/tmp/init-config.yaml
```
이 방식보다 실제로는 `init.sql` 내용을 MySQL 안에서 직접 실행하는 편이 낫습니다.

실행할 SQL은 아래와 같습니다.
```sql
CREATE DATABASE IF NOT EXISTS inventorydb;
USE inventorydb;

CREATE TABLE IF NOT EXISTS inventory (
    product_name VARCHAR(100) PRIMARY KEY,
    stock INT NOT NULL,
    updated_at DATETIME NOT NULL
);

INSERT INTO inventory(product_name, stock, updated_at)
VALUES ('Keyboard', 100, NOW()),
       ('Mouse', 50, NOW()),
       ('Monitor', 20, NOW())
ON DUPLICATE KEY UPDATE updated_at = VALUES(updated_at);
```

### 방법 2. ConfigMap의 SQL만 참고
`k8s/01-inventory-init-configmap.yaml` 안의 SQL을 복사해서 MySQL pod에서 실행

예:
```bash
kubectl exec -it <mysql-pod-name> -- mysql -uroot -p
```

## 확인
- HTML: `http://<ingress-host>/inventory`
- API: `http://<ingress-host>/api/inventory`

## 주의
- 이 서비스는 재고 부족 시 예외를 던집니다.
- 현재는 보상 이벤트나 실패 이벤트 재발행은 포함하지 않았습니다.
- Debezium outbox router가 Kafka로 payload만 전달하는 구성을 가정했습니다.
