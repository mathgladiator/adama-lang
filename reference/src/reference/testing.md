# Testing

Low and behold, Adama has testing via "revertable invariants".
This means that tests can manipulate the document, but the changes are not persisted.
In the devbox, tests can run automatically on every local deployment.

## Mental Model

Since state is so bloody hard to deal with, the best way to leverage Adama's testing is to probe the document and validate invariants based on existing data or data recently added.

# Example from product management suite

```adama
procedure body_create_task(CreateTask ct) {
  if ((iterate _projects where id == ct.project_id)[0] as project) {
    project._tasks <- ct;
  }
}

channel create_task(CreateTask ct) {
  body_create_task(ct);
}

test create_project_with_tasks {
  _projects <- {name: "Name", description:"description"} as p_id;
  CreateTask ct;
  for (int k = 1; k <= 4; k++) {
    ct.name = "task " + k;
    ct.notes = "";
    ct.project_id = p_id;
    body_create_task(ct);
  }
  maybe<Project> project_m = (iterate _projects where id == p_id)[0];
  assert project_m.has();
  if (project_m as project) {
    assert 4 == project._tasks.size();
  }
}
```

At this time, channels can not be invoked, so this is something to be fixed.