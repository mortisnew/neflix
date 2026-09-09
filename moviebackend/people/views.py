
import requests

from django.http import HttpResponse

from rest_framework import viewsets, filters, status
from rest_framework.decorators import action
from rest_framework.response import Response

from permissions import OnlyAdmin
from people.models import Person
from people.serializers import PeopleSerializer


class PeopleViewSet(viewsets.ModelViewSet):
    permission_classes = [OnlyAdmin]
    serializer_class = PeopleSerializer
    queryset = Person.objects.all()

    filter_backends = (filters.SearchFilter,)
    search_fields = ('full_name',)

    @action(detail=True, methods=['get'], url_path='avatar')
    def avatar(self, request, pk=None):
        person = self.get_object()

        if not person.avatar_url:
            return Response(
                {"detail": "This person has no avatar."},
                status=status.HTTP_404_NOT_FOUND
            )

        try:
            image_response = requests.get(
                person.avatar_url,
                timeout=15,
                headers={
                    "User-Agent": "Mozilla/5.0"
                }
            )

            if image_response.status_code != 200:
                return Response(
                    {"detail": "Could not fetch avatar."},
                    status=status.HTTP_502_BAD_GATEWAY
                )

            content_type = image_response.headers.get(
                "Content-Type",
                "image/jpeg"
            )

            return HttpResponse(
                image_response.content,
                content_type=content_type
            )

        except requests.RequestException:
            return Response(
                {"detail": "Avatar server is unreachable."},
                status=status.HTTP_502_BAD_GATEWAY
            )
