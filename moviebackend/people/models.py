from django.db import models

class Person(models.Model):
    avatar_url = models.URLField()
    full_name = models.CharField(max_length=100)
    bio = models.TextField()

    def __str__(self):
        return self.full_name

