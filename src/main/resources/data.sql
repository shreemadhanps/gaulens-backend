-- Seed data for the breeds table.
-- Matches the sample breeds shown on the GauLens frontend so the two line up out of the box.
-- Runs fresh on every startup since the database is in-memory (see application.properties).

INSERT INTO breeds (id, name, type, origin, description) VALUES
  (1, 'Gir', 'CATTLE', 'Gujarat, India', 'A zebu cattle breed known for its distinctively curved horns and high milk yield.'),
  (2, 'Sahiwal', 'CATTLE', 'Punjab region', 'A heat-tolerant dairy breed with loose skin and a reddish-brown coat.'),
  (3, 'Tharparkar', 'CATTLE', 'Rajasthan / Thar desert', 'A dual-purpose, drought-hardy breed with a white-grey coat.'),
  (4, 'Red Sindhi', 'CATTLE', 'Sindh region', 'A compact, heat-tolerant dairy breed with a deep red coat.'),
  (5, 'Murrah', 'BUFFALO', 'Haryana, India', 'India''s leading dairy buffalo breed, jet black with tightly curled horns.'),
  (6, 'Nili-Ravi', 'BUFFALO', 'Punjab (Ravi river belt)', 'Recognisable by its wall eyes and curled horns, often with white markings.'),
  (7, 'Jaffarabadi', 'BUFFALO', 'Gir forest, Gujarat', 'The heaviest buffalo breed, with massive horns and high-fat milk.'),
  (8, 'Surti', 'BUFFALO', 'Gujarat, India', 'A medium-sized buffalo breed valued for high butterfat content in its milk.');

INSERT INTO breed_traits (breed_id, trait) VALUES
  (1, 'Curved horns'),
  (1, 'Reddish coat'),
  (1, 'High milk yield'),
  (2, 'Loose skin'),
  (2, 'Reddish-brown'),
  (2, 'Heat tolerant'),
  (3, 'White-grey coat'),
  (3, 'Drought hardy'),
  (3, 'Dual purpose'),
  (4, 'Compact build'),
  (4, 'Deep red coat'),
  (4, 'Heat tolerant'),
  (5, 'Tightly curled horns'),
  (5, 'Jet black'),
  (5, 'Top milk breed'),
  (6, 'Wall eyes'),
  (6, 'Curled horns'),
  (6, 'White markings'),
  (7, 'Massive horns'),
  (7, 'Heaviest buffalo breed'),
  (7, 'High fat milk'),
  (8, 'Sickle-shaped horns'),
  (8, 'Medium build'),
  (8, 'High butterfat milk');

ALTER TABLE breeds ALTER COLUMN id RESTART WITH 9;
