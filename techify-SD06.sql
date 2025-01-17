USE [Techify]
GO
/****** Object:  Table [dbo].[category]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[category](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[parent_category_id] [int] NULL,
	[name] [nvarchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product](
	[id] [nvarchar](20) NOT NULL,
	[category_id] [int] NULL,
	[name] [nvarchar](50) NOT NULL,
	[thumbnail] [nvarchar](255) NULL,
	[brand] [nvarchar](50) NULL,
	[origin] [nvarchar](50) NOT NULL,
	[unit] [nvarchar](20) NOT NULL,
	[serial] [nvarchar](50) NULL,
	[warranty] [int] NULL,
	[buy_price] [numeric](38, 2) NULL,
	[sell_price] [numeric](38, 2) NULL,
	[tax] [float] NOT NULL,
	[description] [nvarchar](255) NULL,
	[color_id] [tinyint] NULL,
	[image_id] [tinyint] NULL,
	[attribute_id] [tinyint] NULL,
	[created_at] [datetime] NULL,
	[inventory_quantity] [int] NOT NULL,
	[status] [tinyint] NOT NULL,
	[size_id] [tinyint] NULL,
	[available_quantity] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[order]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order](
	[id] [nvarchar](40) NOT NULL,
	[customer_id] [nvarchar](20) NULL,
	[staff_id] [nvarchar](20) NULL,
	[payment_method_id] [tinyint] NULL,
	[transport_vendor_id] [nvarchar](15) NULL,
	[voucher_id] [nvarchar](20) NULL,
	[shipping_address] [nvarchar](255) NULL,
	[status] [tinyint] NOT NULL,
	[created_at] [datetimeoffset](6) NULL,
	[updated_at] [datetimeoffset](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[order_detail]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order_detail](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[order_id] [nvarchar](40) NULL,
	[product_id] [nvarchar](20) NULL,
	[color] [nvarchar](50) NULL,
	[price] [numeric](38, 2) NULL,
	[quantity] [int] NOT NULL,
	[size] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  View [dbo].[vw_top_selling_products]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_top_selling_products] AS
SELECT 
    p.id AS product_id,
    p.name AS product_name,
    p.thumbnail,
    p.sell_price,
    c.name AS category_name,
    ISNULL(SUM(od.quantity), 0) AS total_quantity_sold,
    ISNULL(COUNT(DISTINCT o.id), 0) AS number_of_orders,
    ISNULL(SUM(od.price * od.quantity), 0) AS total_revenue
FROM 
    product p
LEFT JOIN category c ON p.category_id = c.id
LEFT JOIN order_detail od ON p.id = od.product_id
LEFT JOIN [order] o ON od.order_id = o.id
GROUP BY 
    p.id, p.name, p.thumbnail, p.sell_price, c.name;
GO
/****** Object:  Table [dbo].[account]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[account](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[email] [nvarchar](50) NOT NULL,
	[password_hash] [nvarchar](255) NULL,
	[role] [nvarchar](40) NOT NULL,
	[refresh_token] [nvarchar](255) NULL,
	[google_id] [nvarchar](50) NULL,
	[facebook_id] [nvarchar](50) NULL,
	[avatar] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[attribute]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[attribute](
	[id] [tinyint] IDENTITY(1,1) NOT NULL,
	[attribute_json] [nvarchar](4000) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[color]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[color](
	[id] [tinyint] IDENTITY(1,1) NOT NULL,
	[color_json] [nvarchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[customer]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[customer](
	[id] [nvarchar](20) NOT NULL,
	[account_id] [int] NULL,
	[full_name] [nvarchar](50) NOT NULL,
	[phone] [nvarchar](20) NULL,
	[alt_phone] [nvarchar](20) NULL,
	[province] [nvarchar](50) NULL,
	[district] [nvarchar](50) NULL,
	[ward] [nvarchar](50) NULL,
	[address] [nvarchar](255) NULL,
	[alt_address] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[image]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[image](
	[id] [tinyint] IDENTITY(1,1) NOT NULL,
	[image_json] [nvarchar](1000) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[parent_category]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[parent_category](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
	[thumbnail] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[payment_method]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[payment_method](
	[id] [tinyint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_promotion]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_promotion](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[product_id] [nvarchar](20) NULL,
	[promotion_id] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[promotion]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[promotion](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
	[description] [nvarchar](255) NULL,
	[discount_type] [bit] NOT NULL,
	[discount_value] [float] NOT NULL,
	[start_date] [datetimeoffset](6) NULL,
	[end_date] [datetimeoffset](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[review]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[review](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[product_id] [nvarchar](20) NULL,
	[customer_id] [nvarchar](20) NULL,
	[rating] [tinyint] NOT NULL,
	[comment] [nvarchar](255) NULL,
	[created_at] [datetimeoffset](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[size]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[size](
	[id] [tinyint] IDENTITY(1,1) NOT NULL,
	[size_json] [nvarchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[staff]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[staff](
	[id] [nvarchar](20) NOT NULL,
	[account_id] [int] NULL,
	[full_name] [nvarchar](50) NOT NULL,
	[dob] [date] NULL,
	[gender] [nvarchar](10) NULL,
	[citizen_id] [nvarchar](20) NULL,
	[phone] [nvarchar](20) NULL,
	[address] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[transport_vendor]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[transport_vendor](
	[id] [nvarchar](15) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
	[phone] [nvarchar](20) NULL,
	[email] [nvarchar](50) NULL,
	[status] [bit] NOT NULL,
	[base_price] [money] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[vnpay_transaction]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[vnpay_transaction](
	[id] [nvarchar](40) NOT NULL,
	[order_id] [nvarchar](40) NULL,
	[amount] [money] NOT NULL,
	[bank_code] [nvarchar](20) NULL,
	[bank_tran_no] [nvarchar](255) NULL,
	[card_type] [nvarchar](20) NULL,
	[pay_date] [datetime2](7) NULL,
	[transaction_no] [nvarchar](255) NULL,
	[transaction_status] [nvarchar](10) NULL,
	[txn_ref] [nvarchar](255) NOT NULL,
	[created_at] [datetime2](7) NOT NULL,
	[updated_at] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[voucher]    Script Date: 07/01/2025 23:37:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[voucher](
	[id] [nvarchar](20) NOT NULL,
	[discount_type] [bit] NOT NULL,
	[discount_value] [float] NOT NULL,
	[min_order] [numeric](38, 2) NULL,
	[max_discount] [numeric](38, 2) NULL,
	[usage_limit] [int] NULL,
	[start_date] [datetimeoffset](6) NULL,
	[end_date] [datetimeoffset](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[product] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[product] ADD  DEFAULT ((0)) FOR [inventory_quantity]
GO
ALTER TABLE [dbo].[product] ADD  DEFAULT ((1)) FOR [status]
GO
ALTER TABLE [dbo].[product] ADD  DEFAULT ((0)) FOR [available_quantity]
GO
ALTER TABLE [dbo].[transport_vendor] ADD  DEFAULT ((1)) FOR [status]
GO
ALTER TABLE [dbo].[transport_vendor] ADD  DEFAULT ((0)) FOR [base_price]
GO
ALTER TABLE [dbo].[category]  WITH CHECK ADD FOREIGN KEY([parent_category_id])
REFERENCES [dbo].[parent_category] ([id])
GO
ALTER TABLE [dbo].[customer]  WITH CHECK ADD FOREIGN KEY([account_id])
REFERENCES [dbo].[account] ([id])
GO
ALTER TABLE [dbo].[order]  WITH CHECK ADD FOREIGN KEY([customer_id])
REFERENCES [dbo].[customer] ([id])
GO
ALTER TABLE [dbo].[order]  WITH CHECK ADD FOREIGN KEY([payment_method_id])
REFERENCES [dbo].[payment_method] ([id])
GO
ALTER TABLE [dbo].[order]  WITH CHECK ADD FOREIGN KEY([staff_id])
REFERENCES [dbo].[staff] ([id])
GO
ALTER TABLE [dbo].[order]  WITH CHECK ADD FOREIGN KEY([transport_vendor_id])
REFERENCES [dbo].[transport_vendor] ([id])
GO
ALTER TABLE [dbo].[order]  WITH CHECK ADD FOREIGN KEY([voucher_id])
REFERENCES [dbo].[voucher] ([id])
GO
ALTER TABLE [dbo].[order_detail]  WITH CHECK ADD FOREIGN KEY([order_id])
REFERENCES [dbo].[order] ([id])
GO
ALTER TABLE [dbo].[order_detail]  WITH CHECK ADD FOREIGN KEY([product_id])
REFERENCES [dbo].[product] ([id])
GO
ALTER TABLE [dbo].[product]  WITH CHECK ADD FOREIGN KEY([attribute_id])
REFERENCES [dbo].[attribute] ([id])
GO
ALTER TABLE [dbo].[product]  WITH CHECK ADD FOREIGN KEY([category_id])
REFERENCES [dbo].[category] ([id])
GO
ALTER TABLE [dbo].[product]  WITH CHECK ADD FOREIGN KEY([color_id])
REFERENCES [dbo].[color] ([id])
GO
ALTER TABLE [dbo].[product]  WITH CHECK ADD FOREIGN KEY([image_id])
REFERENCES [dbo].[image] ([id])
GO
ALTER TABLE [dbo].[product]  WITH CHECK ADD FOREIGN KEY([size_id])
REFERENCES [dbo].[size] ([id])
GO
ALTER TABLE [dbo].[product_promotion]  WITH CHECK ADD FOREIGN KEY([product_id])
REFERENCES [dbo].[product] ([id])
GO
ALTER TABLE [dbo].[product_promotion]  WITH CHECK ADD FOREIGN KEY([promotion_id])
REFERENCES [dbo].[promotion] ([id])
GO
ALTER TABLE [dbo].[review]  WITH CHECK ADD FOREIGN KEY([customer_id])
REFERENCES [dbo].[customer] ([id])
GO
ALTER TABLE [dbo].[review]  WITH CHECK ADD FOREIGN KEY([product_id])
REFERENCES [dbo].[product] ([id])
GO
ALTER TABLE [dbo].[staff]  WITH CHECK ADD FOREIGN KEY([account_id])
REFERENCES [dbo].[account] ([id])
GO
ALTER TABLE [dbo].[vnpay_transaction]  WITH CHECK ADD FOREIGN KEY([order_id])
REFERENCES [dbo].[order] ([id])
GO
