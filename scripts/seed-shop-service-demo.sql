-- Demo data for shop-service.
-- Run this against the `shop-service` database after shop-service has created tables.

INSERT INTO subscription_plan (
  subscription_plan_name,
  subscription_plan_description,
  price_monthly,
  price_yearly,
  config_limit,
  created_at,
  updated_at,
  status
)
SELECT
  'Starter',
  'Goi co ban cho cua hang moi bat dau.',
  199000,
  1990000,
  '{"max_projects":"1","storage_gb":"5","ai_queries_per_month":"100","max_employees":"10","support":"Email"}'::jsonb,
  now(),
  now(),
  'ACTIVE'
WHERE NOT EXISTS (
  SELECT 1 FROM subscription_plan WHERE subscription_plan_name = 'Starter'
);

INSERT INTO subscription_plan (
  subscription_plan_name,
  subscription_plan_description,
  price_monthly,
  price_yearly,
  config_limit,
  created_at,
  updated_at,
  status
)
SELECT
  'Growth',
  'Goi phu hop cho chuoi cua hang dang tang truong.',
  499000,
  4990000,
  '{"max_projects":"5","storage_gb":"30","ai_queries_per_month":"1000","max_employees":"50","support":"Priority"}'::jsonb,
  now(),
  now(),
  'ACTIVE'
WHERE NOT EXISTS (
  SELECT 1 FROM subscription_plan WHERE subscription_plan_name = 'Growth'
);

INSERT INTO subscription_plan (
  subscription_plan_name,
  subscription_plan_description,
  price_monthly,
  price_yearly,
  config_limit,
  created_at,
  updated_at,
  status
)
SELECT
  'Enterprise',
  'Goi nang cao cho he thong nhieu chi nhanh.',
  999000,
  9990000,
  '{"max_projects":"20","storage_gb":"100","ai_queries_per_month":"5000","max_employees":"200","support":"Dedicated"}'::jsonb,
  now(),
  now(),
  'ACTIVE'
WHERE NOT EXISTS (
  SELECT 1 FROM subscription_plan WHERE subscription_plan_name = 'Enterprise'
);

INSERT INTO shops (
  shop_name,
  address,
  phone,
  email,
  domain,
  status,
  created_at,
  updated_at,
  current_plan_id,
  current_plan_name,
  subscription_status,
  subscription_ended_at
)
SELECT
  'ABC Coffee Demo',
  '123 Nguyen Hue, Quan 1, TP.HCM',
  '0909000001',
  'abc-demo@example.com',
  'abc-shop.com',
  'ACTIVE',
  now(),
  now(),
  p.subscription_plan_id,
  p.subscription_plan_name,
  'ACTIVE',
  now() + interval '1 year'
FROM subscription_plan p
WHERE p.subscription_plan_name = 'Growth'
  AND NOT EXISTS (
    SELECT 1 FROM shops WHERE domain = 'abc-shop.com'
  );
