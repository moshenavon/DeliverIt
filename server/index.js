const express = require('express');
const { randomUUID } = require('crypto');

const app = express();
app.use(express.json());

const VALID_STATUSES = ['PENDING', 'ASSIGNED', 'IN_TRANSIT', 'DELIVERED', 'FAILED'];

let tasks = [];

app.get('/tasks', (req
                   , res) => {
   res.json([...tasks].sort((a, b) => b.createdAt - a.createdAt));
});

app.post('/tasks', (req, res) => {
  const { itemDescription, fromLocation, toLocation } = req.body ?? {};

  if (!itemDescription || !fromLocation || !toLocation) {
    res.status(400).json({ error: 'itemDescription, fromLocation and toLocation are required' });
    return;
  }

  const now = Date.now();
  const task = {
    id: randomUUID(),
    itemDescription,
    fromLocation,
    toLocation,
    status: 'PENDING',
    createdAt: now,
    statusHistory: [{ status: 'PENDING', timestamp: now }]
  };

  tasks.push(task);
  res.status(201).json(task);
});

app.patch('/tasks/:id/status', (req, res) => {
  const { status } = req.body ?? {};

  if (!VALID_STATUSES.includes(status)) {
    res.status(400).json({ error: `status must be one of ${VALID_STATUSES.join(', ')}` });
    return;
  }

  const task = tasks.find((t) => t.id === req.params.id);
  if (!task) {
    res.status(404).json({ error: 'task not found' });
    return;
  }

  task.status = status;
  task.statusHistory.push({ status, timestamp: Date.now() });
  res.json(task);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`DeliverIt server listening on port ${PORT}`);
});
